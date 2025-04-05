package org.zwf.service.serviceImpl;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.zwf.entity.RestBean;
import org.zwf.entity.dto.Article;
import org.zwf.entity.dto.ArticleCollect;
import org.zwf.entity.vo.request.CreateArticleVO;
import org.zwf.entity.vo.request.UpdateArticleVO;
import org.zwf.entity.vo.response.ArticleCollectVo;
import org.zwf.entity.vo.response.ArticleVO;
import org.zwf.exception.BusinessException;
import org.zwf.mapper.ArticleCollectMapper;
import org.zwf.mapper.ArticleLikeMapper;
import org.zwf.mapper.ArticleMapper;
import org.zwf.mapper.CommentMapper;
import org.zwf.service.AccountService;
import org.zwf.service.ArticleService;
import org.zwf.utils.Const;
import org.zwf.utils.FlowUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zwf.utils.AuthUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文章管理相关服务
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private AccountService accountService;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleCollectMapper articleCollectMapper;
    @Resource
    private ArticleLikeMapper articleLikeMapper;
    @Resource
    private FlowUtil flowUtil;
    @Resource
    private AmqpTemplate amqpTemplate;
    @Value("${my-config.resource.upload.previewImage}")
    private String uploadArticlePreviewImagePath;
    @Value("${my-config.resource.project-domain}")
    private String projectDomain;

    /**
     * 新建文章(状态默认为草稿)
     * @param vo 新建草稿表单实体
     * @param status 文章状态状态
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String createArticle(CreateArticleVO vo, String status) {
        String title = vo.getTitle();
        String summary = vo.getSummary();
        String content = vo.getContent();
        Integer authorId = vo.getAuthorId();
        String previewImage = vo.getPreviewImage();

        if(!accountService.isCurrentUser(authorId)) return "非法操作";
        Article article = new Article();
        article.setTitle(title);
        article.setSummary(summary);
        article.setContent(content);
        article.setAuthorId(authorId);
        article.setPreviewImage(previewImage);

        int insert;
        if(status.equals("draft")) {
            insert = articleMapper.insertDraft(article);
        } else {
            article.setStatus(status);
            insert = articleMapper.insertArticle(article);
        }
        if(insert != 1) return "发生了一些错误，请联系管理员";

        return null;
    }

    /**
     * 删除文章
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String deleteArticle(Integer articleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.valueOf(user.getUsername());

        Article article = articleMapper.getArticleById(articleId);
        if(article == null || !article.getAuthorId().equals(userId)) return "非法操作";

        String previewImage = article.getPreviewImage();
        // 将预览图片文件删除
        try {
            if(previewImage != null) {
                // 从 URL 中提取文件名
                String oldFileName = previewImage.substring(previewImage.lastIndexOf("/") + 1);

                // 拼接文件路径
                Path oldFilePath = Paths.get(uploadArticlePreviewImagePath, oldFileName);

                // 删除旧的文件
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件保存失败"+e.getMessage());
        }


        // 删除文章对应的评论
        commentMapper.deleteCommentsByArticleId(articleId);
        // 删除文章对应的收藏
        articleCollectMapper.deleteArticleCollectByArticleId(articleId);

        // 删除文章
        int delete = articleMapper.deletePublishedArticle(articleId);
        if(delete != 1) return "发生了一些错误，请联系管理员";
        return null;
    }

    /**
     * 删除草稿
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String deleteDraft(Integer articleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.valueOf(user.getUsername());

        Article article = articleMapper.getArticleById(articleId);
        if(article == null || !article.getAuthorId().equals(userId)) return "非法操作";

        String previewImage = article.getPreviewImage();
        // 将预览图片文件删除
        try {
            if(previewImage != null) {
                // 从 URL 中提取文件名
                String oldFileName = previewImage.substring(previewImage.lastIndexOf("/") + 1);

                // 拼接文件路径
                Path oldFilePath = Paths.get(uploadArticlePreviewImagePath, oldFileName);

                // 删除旧的文件
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件保存失败"+e.getMessage());
        }

        int delete = articleMapper.deleteDraftArticle(articleId);
        if(delete != 1) return "发生了一些错误，请联系管理员";
        return null;
    }

    /**
     * 更新文章
     * @param type 文章类型，draft表示草稿，approved表示审核通过已发布的文章
     * @param vo 更新文章表单实体
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String updateArticle(String type, UpdateArticleVO vo) {
        Integer userId = vo.getAuthorId();
        Integer articleId = vo.getId();
        if(!accountService.isCurrentUser(userId)) return "非法操作";

        Article article = articleMapper.getArticleById(articleId);
        if(article == null || !article.getAuthorId().equals(userId)) return "非法操作";

        String oldPreviewImage = article.getPreviewImage();
        // 如果对图片进行更新要将原来的图片删除
        try {
            if(oldPreviewImage != null && !oldPreviewImage.equals(vo.getPreviewImage())) {
                // 从 URL 中提取文件名
                String oldFileName = oldPreviewImage.substring(oldPreviewImage.lastIndexOf("/") + 1);

                // 拼接文件路径
                Path oldFilePath = Paths.get(uploadArticlePreviewImagePath, oldFileName);

                // 删除旧的文件
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件保存失败"+e.getMessage());
        }

        String title = vo.getTitle();
        String summary = vo.getSummary();
        String content = vo.getContent();
        String previewImage = vo.getPreviewImage();

        if(type.equals("approved")) {
            articleMapper.updateArticleStatusById(articleId, "pending_review");
        }

        int update = articleMapper.updateArticleById(articleId, title, summary, content, previewImage);
        if(update != 1) return "发生了一些错误，请联系管理员";

        return null;
    }

    /**
     * 获取已发布文章信息(若作者账号已注销，则作者名显示为"账号已注销"，id置为null)
     * @param articleId 文章id
     * @return 响应实体
     */
    @Override
    public RestBean<ArticleVO> getPublishedArticle(Integer articleId) {
        Article article = articleMapper.getArticleById(articleId);
        if(article == null) return RestBean.argumentNotValid("文章不存在");
        if(!article.getStatus().equals("approved")) return RestBean.argumentNotValid("非法操作");

        ArticleVO vo = this.toArticleVO(article);
        // 判断当前用户是否收藏和点赞了该文章
        // 判断用户是否登录
        if (AuthUtil.isLoggedIn()) {
            Integer userId = AuthUtil.getCurrentUserId();
            boolean isCollected = articleCollectMapper.isCollected(userId, articleId);
            boolean isLiked = articleLikeMapper.isLiked(userId, articleId);
            vo.setIsCollected(isCollected);
            vo.setIsLiked(isLiked);
        } else {
            // 未登录用户
            vo.setIsCollected(false);
            vo.setIsLiked(false);
        }
        return RestBean.success(vo);
    }

    /**
     * 获取当前用户草稿信息
     * @param articleId 文章id
     * @return 响应实体
     */
    @Override
    public RestBean<ArticleVO> getDraft(Integer articleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.valueOf(user.getUsername());

        Article article = articleMapper.getArticleById(articleId);
        if(article == null) return RestBean.argumentNotValid("文章不存在");

        if(!article.getStatus().equals("draft") || !article.getAuthorId().equals(userId)) {
            return RestBean.argumentNotValid("非法操作");
        }

        ArticleVO vo = this.toArticleVO(article);
        return RestBean.success(vo);
    }

    /**
     * 获取审核文章信息
     * @param articleId 文章id
     * @return 响应实体
     */
    @Override
    public RestBean<ArticleVO> getReviewingArticle(Integer articleId) {
        Article article = articleMapper.getArticleById(articleId);
        if(article == null) return RestBean.argumentNotValid("文章不存在");
        if(!article.getStatus().equals("reviewing")) return RestBean.argumentNotValid("非法操作");

        ArticleVO vo = this.toArticleVO(article);
        return RestBean.success(vo);
    }

    /**
     * 获取所有已发布文章列表
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getAllPublishedArticle() {
        List<Article> articles = articleMapper.getAllPublishedArticles();

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 获取所有待审核文章
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getAllPendingReviewArticles() {
        List<Article> articles = articleMapper.getAllPendingReviewArticles();

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 获取所有审核中文章
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getAllReviewingArticles() {
        List<Article> articles = articleMapper.getAllReviewingArticles();

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 获取当前用户所有草稿
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getUserDrafts() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.valueOf(user.getUsername());

        List<Article> articles = articleMapper.getArticleByAuthorId(userId);
        articles.removeIf(article -> !article.getStatus().equals("draft"));

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 获取当前用户所有待审核文章
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getUserReviewArticles() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.valueOf(user.getUsername());

        List<Article> articles = articleMapper.getArticleByAuthorId(userId);
        articles.removeIf(article -> !(article.getStatus().equals("pending_review") || article.getStatus().equals("reviewing")));

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 根据作者id获取用户所有已发布和已下架的文章
     * @param authorId 作者id
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getPublishedArticleByAuthorId(Integer authorId) {
        List<Article> articles = articleMapper.getArticleByAuthorId(authorId);
        articles.removeIf(article -> !(article.getStatus().equals("approved") || article.getStatus().equals("take_down")));

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 根据标题获取文章
     * @param title 文章标题
     * @return 响应实体
     */
    @Override
    public RestBean<List<ArticleVO>> getArticleByTitle(String title) {
        List<Article> articles = articleMapper.getArticleByTitle(title);

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 管理员开始审核文章
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String startReview(Integer articleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int reviewerId = Integer.parseInt(user.getUsername());

        String lockKey = Const.REVIEW_LIMIT + articleId;
        String lockValue = Const.REVIEW_VALUE + reviewerId;

        if(!flowUtil.tryLock(lockKey, lockValue, 60)) {
            return "该文章正在被审核，请勿重复操作";
        }

        try {
            Article article = articleMapper.getArticleById(articleId);
            if(article == null) return "文章不存在";
            if(!article.getStatus().equals("pending_review")) return "该文章不在待审核状态，无法开始审核";

            articleMapper.updateArticleStatusById(articleId, "reviewing");
            return null;
        } finally {
            flowUtil.releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 文章审核通过
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Transactional
    @Override
    public String approveReview(Integer articleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int reviewerId = Integer.parseInt(user.getUsername());

        String lockKey = Const.REVIEW_LIMIT + articleId;
        String lockValue = Const.REVIEW_VALUE + reviewerId;

        if(!flowUtil.tryLock(lockKey, lockValue, 60)) {
            return "该文章正在被审核，请勿重复操作";
        }

        try {
            Article article = articleMapper.getArticleById(articleId);
            if(article == null) return "文章不存在";
            if(!article.getStatus().equals("reviewing")) return "该文章不在审核状态，无法开始通过审核";

            articleMapper.updateArticleStatusById(articleId, "approved");
            return null;
        } finally {
            flowUtil.releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 文章审核拒绝通过
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String rejectReview(Integer articleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int reviewerId = Integer.parseInt(user.getUsername());

        String lockKey = Const.REVIEW_LIMIT + articleId;
        String lockValue = Const.REVIEW_VALUE + reviewerId;

        if(!flowUtil.tryLock(lockKey, lockValue, 60)) {
            return "该文章正在被审核，请勿重复操作";
        }

        try {
            Article article = articleMapper.getArticleById(articleId);
            if(article == null) return "文章不存在";
            if(!article.getStatus().equals("reviewing")) return "该文章不在审核状态，无法开始取消通过审核";

            articleMapper.updateArticleStatusById(articleId, "draft");
            return null;
        } finally {
            flowUtil.releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 重置文章审核状态
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String resetReviewing(Integer articleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int reviewerId = Integer.parseInt(user.getUsername());

        String lockKey = Const.REVIEW_LIMIT + articleId;
        String lockValue = Const.REVIEW_VALUE + reviewerId;

        if(!flowUtil.tryLock(lockKey, lockValue, 60)) {
            return "该文章正在被审核，请勿操作";
        }

        try {
            Article article = articleMapper.getArticleById(articleId);
            if(article == null) return "文章不存在";
            if(!article.getStatus().equals("reviewing")) return "该文章不在审核状态，无法开始取消通过审核";

            articleMapper.updateArticleStatusById(articleId, "pending_review");
            return null;
        } finally {
            flowUtil.releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 文章下架
     * @param articleIds 文章id列表
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String takeDownArticle(List<Integer> articleIds) {
        for(Integer id: articleIds) {
            articleMapper.updateArticleStatusById(id, "take_down");
        }
        return null;
    }

    /**
     * 文章恢复
     * @param articleIds 文章id列表
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String recoverArticle(List<Integer> articleIds) {
        for(Integer id: articleIds) {
            articleMapper.updateArticleStatusById(id, "approved");
        }
        return null;
    }

    /**
     * 获取被下架文章列表
     * @return 文章列表
     */
    @Override
    public RestBean<List<ArticleVO>> getTakeDownArticleList() {
        List<Article> articles = articleMapper.getAllTakeDownArticles();

        List<ArticleVO> voList = new ArrayList<>();
        for(Article article: articles) {
            voList.add(this.toArticleVO(article));
        }
        return RestBean.success(voList);
    }

    /**
     * 点赞文章
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    public String toggleLikeArticle(Integer articleId) {
        Article article = articleMapper.getArticleById(articleId);
        if(article == null) return "文章不存在";
        if(!article.getStatus().equals("approved")) return "非法操作";

        Integer userId = AuthUtil.getCurrentUserId();
        if (userId == null) return "请登录后再进行操作";
        boolean isLiked = articleLikeMapper.isLiked(userId, articleId);

        Map<String, Object> msg;
        if(isLiked) {
            // 点赞请求放入消息队列中，由消息队列异步处理点赞
            // 取消点赞
            msg = Map.of("action", "cancelLike", "type", "article", "id", articleId, "userId", userId);
            amqpTemplate.convertAndSend("like", msg);
        } else {
            // 点赞
            msg = Map.of("action", "like", "type", "article", "id", articleId, "userId", userId);
            amqpTemplate.convertAndSend("like", msg);
        }
        return null;
    }

    /**
     * 收藏/取消收藏文章
     * @param articleId 文章id
     * @return 操作结果，null表示正常，否则为错误原因string
     */
    @Override
    @Transactional
    public String toggleCollect(Integer articleId) {
        Article article = articleMapper.getArticleById(articleId);
        if(article == null) return "文章不存在";
        if(!article.getStatus().equals("approved")) return "非法操作";

        Integer userId = AuthUtil.getCurrentUserId();
        if (userId == null) return "请登录后再进行操作";
        boolean isCollected = articleCollectMapper.isCollected(userId, articleId);

        if(isCollected) {
            // 取消收藏
            articleCollectMapper.cancelCollectArticle(userId, articleId);
        } else {
            // 收藏
            articleCollectMapper.collectArticle(userId, articleId);
        }
        return null;
    }

    /**
     * 获取收藏的文章列表
     * @return 文章列表
     */
    @Override
    public RestBean<List<ArticleCollectVo>> getCollectedArticles() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.valueOf(user.getUsername());

        List<ArticleCollect> collects = articleCollectMapper.getUserCollectArticles(userId);

        // 将非发布状态的文章过滤掉
        collects.removeIf(collect -> !collect.getStatus().equals("approved"));

        List<ArticleCollectVo> collectVos = new ArrayList<>();
        for(ArticleCollect collect: collects) {
            collectVos.add(this.toArticleCollectVO(collect));
        }
        return RestBean.success(collectVos);
    }

    /**
     * 上传文章预览图文件
     * @param file 文件
     * @return 响应实体
     */
    @Transactional
    @Override
    public RestBean<Map<String, String>> uploadPreviewImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        try {
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String uniqueFileName = UUID.randomUUID() + fileExtension;

            // 创建目标文件路径
            Path uploadPath = Paths.get(uploadArticlePreviewImagePath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件到指定路径
            Path filePath = uploadPath.resolve(uniqueFileName);
            file.transferTo(filePath.toFile());

            // 生成文件的访问 URL
            String fileUrl = projectDomain + "/articlePreviewImage/" + uniqueFileName;

            return RestBean.success(Map.of("url", fileUrl));
        } catch (IOException e) {
            throw new BusinessException("文件保存失败"+e.getMessage());
        }
    }

    /**
     * 将文章实体转换为文章信息实体
     * @param article 文章实体
     * @return 文章信息实体
     */
    private ArticleVO toArticleVO(Article article) {
        Integer authorId = article.getAuthorId();
        String author = accountService.getUsernameById(authorId);
        if(author == null) {
            author = "账号已注销";
            authorId = null;
        }

        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setAuthorId(authorId);
        vo.setAuthor(author);
        vo.setCreatedAt(article.getCreatedAt());
        vo.setUpdatedAt(article.getUpdatedAt());
        vo.setStatus(article.getStatus());
        vo.setView(article.getView());
        vo.setLike(article.getLike());
        vo.setPreviewImage(article.getPreviewImage());
        return vo;
    }

    /**
     * 将文章收藏实体转换为文章收藏信息实体
     * @param collect 文章收藏实体
     * @return 文章收藏信息实体
     */
    private ArticleCollectVo toArticleCollectVO(ArticleCollect collect) {
        Integer authorId = collect.getAuthorId();
        String author = accountService.getUsernameById(authorId);
        if(author == null) {
            author = "账号已注销";
            authorId = null;
        }

        ArticleCollectVo vo = new ArticleCollectVo();
        vo.setId(collect.getId());
        vo.setTitle(collect.getTitle());
        vo.setSummary(collect.getSummary());
        vo.setAuthorId(authorId);
        vo.setAuthor(author);
        vo.setCreatedAt(collect.getCreatedAt());
        vo.setStatus(collect.getStatus());
        vo.setView(collect.getView());
        vo.setLike(collect.getLike());
        vo.setCollectedTime(collect.getCollectedTime());
        return vo;
    }
}

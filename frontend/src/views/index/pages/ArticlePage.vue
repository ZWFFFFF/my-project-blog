<script setup>
import { onMounted, onUnmounted, ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from "vue-router";
import {collectArticleToggle, getArticle, getDraft, likeArticleToggle} from "@/net/article.js";
import {ArrowDown, ArrowUp, UserFilled} from "@element-plus/icons-vue";
import '@vueup/vue-quill/dist/vue-quill.bubble.css';
import { QuillEditor } from '@vueup/vue-quill'
import {ElMessage} from "element-plus";
import {formatTimestamp, throttle} from "@/net/utils.js";
import {useStore} from "vuex";
import Button from "@/components/Button.vue";
import {creatComment, deleteComment, cancelLikeComment, getComments, likeComment} from "@/net/comment.js";

const route = useRoute();
const router = useRouter();
const store = useStore()
const editorRef = ref()
const articleType = ref(route.params.type)
const articleId = ref(route.params.id)
const article = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  authorId: null,
  author: '',
  createdAt: '',
  updatedAt: '',
  view: null,
  like: null,
  isCollected: null,
  isLiked: null
})
const isCommentInputExpanded = ref(false);
const commentText = ref('');
const commentContainer = ref(null);
const comments = ref([])
const activeReplyForms = ref({}) // 存储当前激活的回复表单, eg.{ 123: true, 456: false }
const replyTexts = ref({}) // 存储回复内容, eg.{ 123: '回复内容', 456: '回复内容' }
const sortOption = ref('hot') // 评论排序 'latest' 或 'hot'
const sortedComments = computed(() => {
  // 分离新评论和普通评论
  const newComments = comments.value.filter(c => c.isNewComment)
  const normalComments = comments.value.filter(c => !c.isNewComment)

  // 只对普通评论排序
  const sortedNormalComments = [...normalComments]
  if (sortOption.value === 'latest') {
    sortedNormalComments.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else {
    sortedNormalComments.sort((a, b) => b.initialLike - a.initialLike)
  }

  // 新评论在前，后面是排序后的普通评论
  return [...newComments, ...sortedNormalComments]
})

const fetchData = () => {
  if(articleType.value === 'approved') {
    // 获取文章信息
    getArticle(articleId.value, (data) => {
      Object.assign(article, data)
      // 不需要检测userId，因为任何人都可以看已发布的文章
    })
    // 获取评论信息
    getComments(articleId.value, (data) => {
      // 递归函数添加属性
      const addProperty = (comments) => comments.map(comment => ({
        ...comment,
        isLiked: false,
        showReplies: false,
        initialLike: comment.like, // 保存初始点赞数用于排序
        replies: comment.replies ? addProperty(comment.replies) : []
      }))

      comments.value = addProperty(data)
    })
  } else if(articleType.value === 'draft') {
    getDraft(articleId.value, (data) => {
      Object.assign(article, data)
      // 访问草稿需要检测userId，因为必须时登录的用户才可以看自己的草稿
      if(article.authorId !== store.state.user.id) {
        ElMessage.error('非法操作')
        router.push('/')
      }
    })
  }
}

const articleLike = () => {
  if(!store.state.user.id) {
    ElMessage.warning('请先登录')
  }

  if(articleType.value === 'approved') {
    if(!article.isLiked) {
      likeArticleToggle(articleId.value, () => {
        article.isLiked = true
        article.like++
      })
    } else {
      likeArticleToggle(articleId.value, () => {
        article.isLiked = false
        article.like--
      })
    }
  }
}

const handleArticleLike = throttle(articleLike, 500)

const collect = () => {
  if(!store.state.user.id) {
    ElMessage.warning('请先登录')
  }

  if(articleType.value === 'approved') {
    if(!article.isCollected) {
      collectArticleToggle(articleId.value, () => {
        article.isCollected = true
      })
    } else {
      collectArticleToggle(articleId.value, () => {
        article.isCollected = false
      })
    }
  }
}

const handleCollect = throttle(collect, 500)

const share = () => {
  if(articleType.value === 'approved') {
    navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制到剪贴板')
  }
}

const handleShare = throttle(share, 1000)

const expandCommentTextarea = () => isCommentInputExpanded.value = true;

const collapseCommentTextarea = () => {
  if (isCommentInputExpanded.value) {
    commentText.value = '';
    isCommentInputExpanded.value = false;
  }
};

const handleClickCommentTextareaOutside = (e) => {
  if (commentContainer.value && !commentContainer.value.contains(e.target)) {
    collapseCommentTextarea();
  }
}

const submitComment = () => {
  if(!store.state.user.id) {
    ElMessage.warning('请先登录')
  }

  if(articleType.value !== 'approved') return

  if (!commentText.value || commentText.value.trim() === '') {
    ElMessage.warning('评论内容不能为空')
    return
  }

  if(!confirm("确定要发送该评论吗？")) {
    return
  }

  const comment = {
    articleId: Number(articleId.value),
    userId: store.state.user.id,
    content: commentText.value
  }

  creatComment(comment, (data) => {
    // 添加新评论到前端状态
    comments.value.unshift({
      ...data,
      isLiked: false,
      showReplies: false,
      replies: [],
      isNewComment: true // 用于标记新评论顶置
    })

    // 清空输入框
    commentText.value = ''
    isCommentInputExpanded.value = false
  })
}

const handleCommentSubmit = throttle(submitComment, 500)

// 显示评论的回复信息
const handleShowReplies = (commentId) => {
  const comment = comments.value.find((comment) => comment.id === commentId);
  if (comment) {
    comment.showReplies = !comment.showReplies;
  }
}

const commentLike = (commentId, isLiked) => {
  if(!store.state.user.id) {
    ElMessage.warning('请先登录')
  }

  if(articleType.value !== 'approved') return

  if(isLiked === false) {
    likeComment(commentId, () => {
      updateCommentLikeProperty(commentId, comments.value)
    })
  } else {
    cancelLikeComment(commentId, () => {
      updateCommentLikeProperty(commentId, comments.value)
    })
  }
}

const handleCommentLike = throttle(commentLike, 500)

// 更新前端点赞数显示
function updateCommentLikeProperty(commentId, comments) {
  for(const comment of comments)  {
    if(comment.id === commentId) {
      comment.isLiked = !comment.isLiked
      if (comment.isLiked) {
        comment.like++
      } else {
        comment.like--
      }
      return true // 找到并修改成功
    }
    if (comment.replies?.length > 0) {
      const found = updateCommentLikeProperty(commentId, comment.replies);
      if(found) return true // 如果在子回复中找到，提前终止搜索
    }
  }
  return false // 未找到该评论
}

// 切换回复表单显示状态的函数
const toggleReplyForm = (commentId) => {
  activeReplyForms.value[commentId] = !activeReplyForms.value[commentId]
  replyTexts.value[commentId] = '' // 清空回复内容
}

// 提交评论的函数
const submitReply = (commentId) => {
  if(!store.state.user.id) {
    ElMessage.warning('请先登录')
  }

  if(articleType.value !== 'approved') return;

  const content = replyTexts.value[commentId]
  if (!content || content.trim() === '') {
    ElMessage.warning('回复内容不能为空')
    return
  }

  if(!confirm("确定要回复该评论吗？")) {
    return
  }

  const comment = {
    parentId: commentId,
    articleId: Number(articleId.value),
    userId: store.state.user.id,
    content: content
  }

  creatComment(comment, (data) => {
    // 更新数据显示
    // 找到父评论并添加回复
    const parentComment = findCommentById(comments.value, commentId)
    if (parentComment) {
      if (!parentComment.replies) {
        parentComment.replies = []
      }
      parentComment.replies.unshift({
        ...data,
        isLiked: false
      })

      // 自动展开回复区域
      parentComment.showReplies = true

      // 清空回复表单
      replyTexts.value[commentId] = ''
      activeReplyForms.value[commentId] = false
    }
  })
}

// 辅助函数：递归查找评论
function findCommentById(comments, id) {
  for (const comment of comments) {
    if (comment.id === id) return comment
    if (comment.replies?.length > 0) {
      const found = findCommentById(comment.replies, id)
      if (found) return found
    }
  }
  return null
}

const handleReplySubmit = throttle(submitReply, 500)

const deleteMyComment = (commentId) => {
  if(articleType.value !== 'approved') return

  if(confirm("确定要删除该评论吗？")) {
    deleteComment(commentId, () => {
      // 从前端状态中移除评论
      removeCommentFromState(comments.value, commentId)
    })
  }
}

// 辅助函数：递归删除评论
function removeCommentFromState(comments, id) {
  for (let i = 0; i < comments.length; i++) {
    if (comments[i].id === id) {
      comments.splice(i, 1)
      return true
    }
    if (comments[i].replies?.length > 0) {
      if (removeCommentFromState(comments[i].replies, id)) {
        return true
      }
    }
  }
  return false
}

const handleDeleteMyComment = throttle(deleteMyComment, 500)

onMounted(() => {
  fetchData()
  // 点击外部区域收起评论输入框
  document.addEventListener('click', handleClickCommentTextareaOutside);
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickCommentTextareaOutside);
})

</script>

<template>
  <div class="bg-white pt-20">
    <div class="container mx-auto w-1/2">
      <div>
        <div class="px-2 pb-5 mb-10 border-b">
          <div class="flex flex-col justify-center items-center mb-10">
            <div class="mb-10 break-words w-full text-center"><p class="font-extrabold text-4xl">{{ article.title }}</p></div>
            <div class="mb-4 break-words w-full text-center"><p>{{ article.summary }}</p></div>
          </div>
          <div class="flex flex-col justify-center items-center gap-2 mb-5">
            <a>
              <el-avatar :icon="UserFilled"></el-avatar>
            </a>
            <span>{{ article.author }}</span>
          </div>
          <div class="text-center">
            <span class="font-bold">发布于：</span><span class="text-gray-400">{{ formatTimestamp(article.createdAt) }}</span>
          </div>
        </div>
        <div class="px-2 mb-10">
          <QuillEditor
              ref="editorRef"
              theme="bubble"
              contentType="html"
              :content="article.content"
              :readOnly="true"
          >

          </QuillEditor>
        </div>
        <div class="flex items-center justify-between mx-10 mb-20">
          <div>
            <el-tooltip
                content="点赞"
                placement="top"
            >
              <button
                  class="flex items-center justify-center gap-2"
                  @click="handleArticleLike"
              >
                <span v-show="!article.isLiked" class="text-zinc-500 hover:text-zinc-950 transition">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="none" stroke-width="1.5" stroke="currentColor" class="size-5">
                    <path d="M1 8.25a1.25 1.25 0 1 1 2.5 0v7.5a1.25 1.25 0 1 1-2.5 0v-7.5ZM11 3V1.7c0-.268.14-.526.395-.607A2 2 0 0 1 14 3c0 .995-.182 1.948-.514 2.826-.204.54.166 1.174.744 1.174h2.52c1.243 0 2.261 1.01 2.146 2.247a23.864 23.864 0 0 1-1.341 5.974C17.153 16.323 16.072 17 14.9 17h-3.192a3 3 0 0 1-1.341-.317l-2.734-1.366A3 3 0 0 0 6.292 15H5V8h.963c.685 0 1.258-.483 1.612-1.068a4.011 4.011 0 0 1 2.166-1.73c.432-.143.853-.386 1.011-.814.16-.432.248-.9.248-1.388Z" />
                  </svg>
                </span>
                <span v-show="article.isLiked">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-5">
                    <path d="M1 8.25a1.25 1.25 0 1 1 2.5 0v7.5a1.25 1.25 0 1 1-2.5 0v-7.5ZM11 3V1.7c0-.268.14-.526.395-.607A2 2 0 0 1 14 3c0 .995-.182 1.948-.514 2.826-.204.54.166 1.174.744 1.174h2.52c1.243 0 2.261 1.01 2.146 2.247a23.864 23.864 0 0 1-1.341 5.974C17.153 16.323 16.072 17 14.9 17h-3.192a3 3 0 0 1-1.341-.317l-2.734-1.366A3 3 0 0 0 6.292 15H5V8h.963c.685 0 1.258-.483 1.612-1.068a4.011 4.011 0 0 1 2.166-1.73c.432-.143.853-.386 1.011-.814.16-.432.248-.9.248-1.388Z" />
                  </svg>
                </span>
                <span class="text-sm text-zinc-500">{{ article.like === null ? 0 : article.like }}</span>
              </button>
            </el-tooltip>
          </div>
          <div class="flex gap-8">
            <el-tooltip
                content="收藏"
                placement="top"
            >
              <button
                  class="flex items-center justify-center"
                  @click="handleCollect"
              >
                <span v-show="!article.isCollected" class="text-zinc-500 hover:text-zinc-950 transition">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="none" stroke-width="1.5" stroke="currentColor" class="size-5">
                    <path fill-rule="evenodd" d="M10 2c-1.716 0-3.408.106-5.07.31C3.806 2.45 3 3.414 3 4.517V17.25a.75.75 0 0 0 1.075.676L10 15.082l5.925 2.844A.75.75 0 0 0 17 17.25V4.517c0-1.103-.806-2.068-1.93-2.207A41.403 41.403 0 0 0 10 2Z" clip-rule="evenodd" />
                  </svg>
                </span>
                <span v-show="article.isCollected">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-5">
                    <path fill-rule="evenodd" d="M10 2c-1.716 0-3.408.106-5.07.31C3.806 2.45 3 3.414 3 4.517V17.25a.75.75 0 0 0 1.075.676L10 15.082l5.925 2.844A.75.75 0 0 0 17 17.25V4.517c0-1.103-.806-2.068-1.93-2.207A41.403 41.403 0 0 0 10 2Z" clip-rule="evenodd" />
                  </svg>
                </span>
              </button>
            </el-tooltip>
            <el-tooltip
                content="分享"
                placement="top"
            >
              <button
                  class="flex items-center justify-center"
                  @click="handleShare"
              >
                <span class="text-zinc-500 hover:text-zinc-950 transition">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-5">
                    <path d="M13 4.5a2.5 2.5 0 1 1 .702 1.737L6.97 9.604a2.518 2.518 0 0 1 0 .792l6.733 3.367a2.5 2.5 0 1 1-.671 1.341l-6.733-3.367a2.5 2.5 0 1 1 0-3.475l6.733-3.366A2.52 2.52 0 0 1 13 4.5Z" />
                  </svg>
                </span>
              </button>
            </el-tooltip>
          </div>
        </div>
      </div>

      <!-- 评论 -->
      <div class="border-t">
        <div class="mb-5 py-4">
          <span class="font-bold text-lg">评论区</span>
        </div>
        <div class="flex flex-col items-start">
          <div class="mb-4 flex items-center gap-2">
            <el-avatar :icon="UserFilled" :size="40"></el-avatar>
            <span>{{ article.author }}</span>
          </div>
          <!-- 评论输入框 -->
          <div class="w-full" ref="commentContainer">
            <div class="flex flex-col gap-4 p-4 rounded-xl bg-[#F6F6F6]">
              <textarea
                  class="w-full bg-[#F6F6F6] leading-6 outline-none resize-none break-words ease-in-out transition-all duration-300"
                  :class="{ 'h-6': !isCommentInputExpanded, 'h-[100px]': isCommentInputExpanded }"
                  type="text"
                  placeholder="有什么想说的..."
                  v-model="commentText"
                  @click="expandCommentTextarea"
                  maxlength="300"
              />
              <div
                  class="flex justify-end transition-opacity duration-300 ease-in-out"
                  :class="{ 'opacity-0': !isCommentInputExpanded, 'opacity-100': isCommentInputExpanded }"
                  v-show="isCommentInputExpanded"
              >
                <button
                    class="bg-zinc-300 rounded-3xl py-2 px-4 text-white transition-colors"
                    @click="handleCommentSubmit"
                >
                  发送
                </button>
              </div>
            </div>
          </div>
          <!-- 评论排序 -->
          <div class="w-full flex justify-between items-center mt-10 py-5 border-b">
            <div>
              <span class="font-bold">共{{ comments.length }}条评论</span>
            </div>
            <div class="flex gap-4">
              <button
                  @click="sortOption = 'hot'"
                  :class="[sortOption === 'hot' ? 'text-zinc-800' : 'text-zinc-400']"
              >
                <span class="font-bold">热门</span>
              </button>
              <button
                  @click="sortOption = 'latest'"
                  :class="[sortOption === 'latest' ? 'text-zinc-800' : 'text-zinc-400']"
              >
                <span class="font-bold">最新</span>
              </button>
            </div>
          </div>
          <!- 评论列表 -->
          <div class="space-y-6 my-10 w-full">
            <div
                v-for="comment in sortedComments"
                :key="comment.id"
                class="flex gap-4 w-full"
            >
              <el-avatar :icon="UserFilled" :size="40"></el-avatar>
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <span class="font-medium text-sm">{{ comment.user.username }}</span>
                  <span class="text-xs text-gray-500">{{ formatTimestamp(comment.createTime) }}</span>
                </div>
                <div class="mb-4">
                  <p class="text-sm">{{ comment.content }}</p>
                </div>
                <div class="flex items-center gap-4">
                  <button
                      class="flex items-center justify-center gap-2"
                      @click="handleCommentLike(comment.id, comment.isLiked)"
                  >
                    <span v-show="comment.isLiked === false" class="text-zinc-500 hover:text-zinc-950 transition">
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="none" stroke-width="1.5" stroke="currentColor" class="size-4">
                        <path d="M2.09 15a1 1 0 0 0 1-1V8a1 1 0 1 0-2 0v6a1 1 0 0 0 1 1ZM5.765 13H4.09V8c.663 0 1.218-.466 1.556-1.037a4.02 4.02 0 0 1 1.358-1.377c.478-.292.907-.706.989-1.26V4.32a9.03 9.03 0 0 0 0-2.642c-.028-.194.048-.394.224-.479A2 2 0 0 1 11.09 3c0 .812-.08 1.605-.235 2.371a.521.521 0 0 0 .502.629h1.733c1.104 0 2.01.898 1.901 1.997a19.831 19.831 0 0 1-1.081 4.788c-.27.747-.998 1.215-1.793 1.215H9.414c-.215 0-.428-.035-.632-.103l-2.384-.794A2.002 2.002 0 0 0 5.765 13Z" />
                      </svg>
                    </span>
                    <span v-show="comment.isLiked" class="text-zinc-500 hover:text-zinc-950 transition">
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="size-4">
                        <path d="M2.09 15a1 1 0 0 0 1-1V8a1 1 0 1 0-2 0v6a1 1 0 0 0 1 1ZM5.765 13H4.09V8c.663 0 1.218-.466 1.556-1.037a4.02 4.02 0 0 1 1.358-1.377c.478-.292.907-.706.989-1.26V4.32a9.03 9.03 0 0 0 0-2.642c-.028-.194.048-.394.224-.479A2 2 0 0 1 11.09 3c0 .812-.08 1.605-.235 2.371a.521.521 0 0 0 .502.629h1.733c1.104 0 2.01.898 1.901 1.997a19.831 19.831 0 0 1-1.081 4.788c-.27.747-.998 1.215-1.793 1.215H9.414c-.215 0-.428-.035-.632-.103l-2.384-.794A2.002 2.002 0 0 0 5.765 13Z" />
                      </svg>
                    </span>
                    <span class="text-sm text-zinc-500">{{ comment.like }}</span>
                  </button>
                  <button
                      class="text-zinc-500 text-sm"
                      @click="toggleReplyForm(comment.id)"
                  >
                    回复
                  </button>
                  <button
                      v-if="article.authorId === store.state.user.id || comment.user.id === store.state.user.id"
                      class="text-zinc-500 text-sm"
                      @click="handleDeleteMyComment(comment.id)"
                  >
                    删除
                  </button>
                </div>
                <!-- 回复表单 (默认隐藏) -->
                <div class="w-full mt-4" v-if="activeReplyForms[comment.id]">
                  <div class="flex flex-col gap-4 p-4 rounded-xl bg-[#F6F6F6]">
                    <textarea
                        class="w-full bg-[#F6F6F6] leading-6 outline-none resize-none break-words h-[100px]"
                        type="text"
                        placeholder="有什么想说的..."
                        v-model="replyTexts[comment.id]"
                        maxlength="300"
                    />
                    <div class="flex justify-end gap-2">
                      <button
                          class="rounded-3xl py-2 px-4 text-gray-400 transition-colors hover:bg-gray-200"
                          @click="toggleReplyForm(comment.id)"
                      >
                        取消
                      </button>
                      <button
                          class="bg-zinc-300 rounded-3xl py-2 px-4 text-white transition-colors"
                          @click="handleReplySubmit(comment.id)"
                      >
                        发送
                      </button>
                    </div>
                  </div>
                </div>
                <div
                    v-if="comment.replies && comment.replies.length !== 0"
                    class="mt-2"
                >
                  <button
                      class="flex items-center gap-2 text-zinc-500 text-sm py-2 px-4 hover:bg-zinc-100 rounded-2xl"
                      @click="handleShowReplies(comment.id)"
                  >
                    <el-icon v-show="comment.showReplies === false"><ArrowDown /></el-icon>
                    <el-icon v-show="comment.showReplies"><ArrowUp /></el-icon>
                    <span>{{ comment.replies.length }}条回复</span>
                  </button>
                  <div
                      v-if="comment.showReplies"
                      v-for="reply in comment.replies"
                  >
                    <div class="flex gap-4 w-full mt-2">
                      <el-avatar :icon="UserFilled" :size="40"></el-avatar>
                      <div class="flex-1">
                        <div class="flex items-center gap-2 mb-2">
                          <span class="font-medium text-sm">{{ reply.user.username }}</span>
                          <span class="text-xs text-gray-500">{{ formatTimestamp(reply.createTime) }}</span>
                        </div>
                        <div class="mb-4">
                          <p class="text-sm">{{ reply.content }}</p>
                        </div>
                        <div class="flex items-center gap-2">
                          <button
                              @click="handleCommentLike(reply.id, reply.isLiked)"
                              class="flex items-center justify-center gap-2"
                          >
                            <span v-show="reply.isLiked === false" class="text-zinc-500 hover:text-zinc-950 transition">
                              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="none" stroke-width="1.5" stroke="currentColor" class="size-4">
                                <path d="M2.09 15a1 1 0 0 0 1-1V8a1 1 0 1 0-2 0v6a1 1 0 0 0 1 1ZM5.765 13H4.09V8c.663 0 1.218-.466 1.556-1.037a4.02 4.02 0 0 1 1.358-1.377c.478-.292.907-.706.989-1.26V4.32a9.03 9.03 0 0 0 0-2.642c-.028-.194.048-.394.224-.479A2 2 0 0 1 11.09 3c0 .812-.08 1.605-.235 2.371a.521.521 0 0 0 .502.629h1.733c1.104 0 2.01.898 1.901 1.997a19.831 19.831 0 0 1-1.081 4.788c-.27.747-.998 1.215-1.793 1.215H9.414c-.215 0-.428-.035-.632-.103l-2.384-.794A2.002 2.002 0 0 0 5.765 13Z" />
                              </svg>
                            </span>
                            <span v-show="reply.isLiked" class="text-zinc-500 hover:text-zinc-950 transition">
                              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="size-4">
                                <path d="M2.09 15a1 1 0 0 0 1-1V8a1 1 0 1 0-2 0v6a1 1 0 0 0 1 1ZM5.765 13H4.09V8c.663 0 1.218-.466 1.556-1.037a4.02 4.02 0 0 1 1.358-1.377c.478-.292.907-.706.989-1.26V4.32a9.03 9.03 0 0 0 0-2.642c-.028-.194.048-.394.224-.479A2 2 0 0 1 11.09 3c0 .812-.08 1.605-.235 2.371a.521.521 0 0 0 .502.629h1.733c1.104 0 2.01.898 1.901 1.997a19.831 19.831 0 0 1-1.081 4.788c-.27.747-.998 1.215-1.793 1.215H9.414c-.215 0-.428-.035-.632-.103l-2.384-.794A2.002 2.002 0 0 0 5.765 13Z" />
                              </svg>
                            </span>
                            <span class="text-sm text-zinc-500">{{ reply.like }}</span>
                          </button>
                          <button
                              v-if="article.authorId === store.state.user.id || reply.user.id === store.state.user.id"
                              class="text-zinc-500 text-sm"
                              @click="handleDeleteMyComment(reply.id)"
                          >
                            删除
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="text-center">
              <span v-if="comments.length > 0" class="font-bold text-zinc-400">没有更多评论了</span>
              <span v-else class="font-bold text-zinc-400">还没有评论哦</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <el-backtop :right="100" :bottom="100" />
</template>

<style scoped>

</style>
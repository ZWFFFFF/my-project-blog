INSERT INTO `my-project-blog`.comment (id, parentId, content, articleId, userId, createTime, `like`) VALUES (4, null, '这是一条父评论', 20, 15, '2025-03-20 00:29:34', 2);
INSERT INTO `my-project-blog`.comment (id, parentId, content, articleId, userId, createTime, `like`) VALUES (5, null, '这是第一条父评论', 32, 15, '2025-03-28 09:15:20', 2);
INSERT INTO `my-project-blog`.comment (id, parentId, content, articleId, userId, createTime, `like`) VALUES (6, 5, '这是第一条子评论', 32, 14, '2025-03-28 09:15:20', 1);
INSERT INTO `my-project-blog`.comment (id, parentId, content, articleId, userId, createTime, `like`) VALUES (7, 5, '这是第二条子评论', 32, 14, '2025-03-28 09:15:20', 1);
INSERT INTO `my-project-blog`.comment (id, parentId, content, articleId, userId, createTime, `like`) VALUES (8, null, '这是第二条父评论', 32, 14, '2025-03-28 09:16:12', 1);
INSERT INTO `my-project-blog`.comment (id, parentId, content, articleId, userId, createTime, `like`) VALUES (11, null, '这是第三条父评论', 32, 15, '2025-03-31 15:07:03', 1);

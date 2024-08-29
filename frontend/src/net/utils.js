function formatTimestamp(timestamp)  {
    if(timestamp === undefined || timestamp === null || timestamp === '') {
        return ''
    }

    const date = new Date(timestamp);
    const now = new Date();

    // 获取时间差
    const diffInMilliseconds = now - date;
    const diffInMinutes = Math.floor(diffInMilliseconds / (1000 * 60));
    const diffInHours = Math.floor(diffInMilliseconds / (1000 * 60 * 60));

    if (date.toDateString() === now.toDateString()) {
        // 今天之内
        if(diffInHours < 1) {
            if(diffInMinutes < 1) {
                return '刚刚';
            } else {
                return `${diffInMinutes}分钟前`;
            }
        } else {
            return `${diffInHours}小时前`;
        }
    } else {
        // 超过一天，显示日期
        return date.toISOString().split('T')[0];
    }
}

export { formatTimestamp };
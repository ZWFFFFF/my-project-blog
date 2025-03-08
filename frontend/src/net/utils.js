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

// 将时间转换为xxxx-xx-xx-xx-xx
function convertToLocalTime(dateString) {
    const date = new Date(dateString); // 解析时间
    if (isNaN(date.getTime())) {
        throw new Error(`Invalid date string: ${dateString}`);
    }
    // 转换为东八区时间
    const offset = 8; // 东八区偏移量
    const localTime = new Date(date.getTime() + offset * 60 * 60 * 1000);
    // 格式化时间为 xxxx-xx-xx-时-分
    const year = localTime.getUTCFullYear();
    const month = String(localTime.getUTCMonth() + 1).padStart(2, '0');
    const day = String(localTime.getUTCDate()).padStart(2, '0');
    const hours = String(localTime.getUTCHours()).padStart(2, '0');
    const minutes = String(localTime.getUTCMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}-${hours}-${minutes}`;
}

/**
 * 节流函数
 * @param {Function} fn - 需要节流的函数
 * @param {number} delay - 节流时间间隔，单位毫秒
 * @returns {Function} - 返回一个节流后的函数
 */
function throttle(fn, delay) {
    let lastCallTime = 0; // 记录上次调用的时间
    return function (...args) {
        const now = Date.now();
        if (now - lastCallTime >= delay) {
            lastCallTime = now;
            fn.apply(this, args); // 调用原始函数，并传递正确的 `this` 和参数
        }
    };
}

export { formatTimestamp, convertToLocalTime, throttle };
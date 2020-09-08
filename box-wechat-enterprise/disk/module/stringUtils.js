//字符串 ""
function isEmpty(param) {
    if (param == undefined || param === '') {
        return true;
    }
    return false;
}

module.exports = {
    isEmpty
};
//对象为空 {}
function isEmpty(param) {
    if (param == undefined || param == '' || param == {}) {
        return true;
    }
    return false;
}

module.exports = {
    isEmpty
};
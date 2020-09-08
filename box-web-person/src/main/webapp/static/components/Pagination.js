(function($){
    $.fn.extend({
        Pagination : function()
        {
            var self = this
            var currentPageNode
            var totalPagesNode
            var totalSizeNode
            var firstPageButton
            var prePageButton
            var nextPageButton
            var lastPageButton

            var _currentPage = 1
            var _totalPages = 1

            function firstPageButton_Click()
            {
                _currentPage = 1
                if(self.onPageChange){
                    self.onPageChange(_currentPage)
                }
            }

            function prePageButton_Click()
            {
                _currentPage -= 1
                if(self.onPageChange){
                    self.onPageChange(_currentPage)
                }
            }

            function nextPageButton_Click()
            {
                _currentPage += 1
                if(self.onPageChange){
                    self.onPageChange(_currentPage)
                }
            }

            function lastPageButton_Click()
            {
                _currentPage = _totalPages
                if(self.onPageChange){
                    self.onPageChange(_currentPage)
                }
            }

            self.setTotalSize = function(totalSize)
            {
                totalSizeNode.text('总记录数：' +  totalSize)
            }

            self.setCurrentPage = function(page)
            {
                _currentPage = page
                currentPageNode.text('当前页：' + _currentPage)
            }

            self.setTotalPages = function(totalPages)
            {
                _totalPages = totalPages
                totalPagesNode.text('总页数：' + _totalPages)

                if(_totalPages > 1){
                    firstPageButton.removeAttr("disabled")
                    prePageButton.removeAttr("disabled")
                    nextPageButton.removeAttr("disabled")
                    lastPageButton.removeAttr("disabled")
                    if(_currentPage == 1){
                        firstPageButton.attr("disabled", "disabled")
                        prePageButton.attr("disabled", "disabled")
                    }
                    if(_currentPage == _totalPages){
                        nextPageButton.attr("disabled", "disabled")
                        lastPageButton.attr("disabled", "disabled")
                    }
                }else{
                    firstPageButton.attr("disabled", "disabled")
                    prePageButton.attr("disabled", "disabled")
                    nextPageButton.attr("disabled", "disabled")
                    lastPageButton.attr("disabled", "disabled")
                }
            }

            self.init = function()
            {
                totalSizeNode = self.find("#totalSize")
                currentPageNode = self.find('#currentPage')
                totalPagesNode = self.find('#totalPages')

                firstPageButton = self.find('#firstPage').on('click', firstPageButton_Click)
                prePageButton = self.find('#prePage').on('click', prePageButton_Click)
                nextPageButton = self.find('#nextPage').on('click', nextPageButton_Click)
                lastPageButton = self.find('#lastPage').on('click', lastPageButton_Click)

                buttons = self.find('button')
            }

            return self
        }
    })
})(jQuery)
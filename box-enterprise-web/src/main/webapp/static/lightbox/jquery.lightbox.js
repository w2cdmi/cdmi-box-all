/**
 * jQuery Lightbox
 * @author Warren Krewenki
 *
 * This package is distributed under the BSD license.
 * For full license information, see LICENSE.TXT
 *
 * Based on Lightbox 2 by Lokesh Dhakar (http://www.huddletogether.com/projects/lightbox2/)
 *
 *
 **/

(function($) {
	$.fn.lightbox = function(options) {
		var rotateNum = 0;
		// build main options
		var opts = $.extend({}, $.fn.lightbox.defaults, options);
        
		$(window).resize(resizeOverlayToFitWindow);
        
		// initialize the lightbox
		initialize();
		showLightbox($(this).get(0));
		return false;

		/*
		# Initialize the lightbox by creating our html and reading some image data
		# This method is called by the constructor after any click events trigger it
		# You will never call it by itself, to my knowledge.
		*/
		function initialize() {
			$('#overlay, #lightbox').remove();
			opts.inprogress = false;
    
			var outerImage = '<div id="outerImageContainer"><div id="imageContainer"><iframe id="lightboxIframe"></iframe><img id="lightboxImage" /><div id="hoverNav"><a href="javascript://" id="prevLink"></a><a href="javascript://" id="nextLink"></a></div><div id="loading"><a href="javascript://" id="loadingLink"></a></div></div></div>';
			var imageData = '<div id="imageDataContainer" class="clearfix"><div id="imageData"><div id="bottomNav"><a href="javascript://" id="bottomNavClose" title="' + opts.strings.closeTitle + '"></a></div><div id="imageDetails"><span id="caption"></span></div></div></div>';
			var imageHandler = '<div id="imageHandlerContainer" class="clearfix">'+
								'<span id="rotateR" title="' + opts.strings.rotateR + '"></span>'+
								'<span id="rotateL" title="' + opts.strings.rotateL + '"></span>'+
								'<span id="btnDownload" title="' + opts.strings.download + '"></span>'+
								'<span id="btnPrev" title="' + opts.strings.prev + '"></span>'+
								'<span id="numberDisplay"></span>'+
								'<span id="btnNext" title="' + opts.strings.next + '"></span>'+
								'</div>';
			
			var string = '<div id="overlay"></div><div id="lightbox">' + imageData + outerImage + imageHandler + '</div>';
				$("body").append(string);

			if (opts.imageScroll === true) {
		        $('#lightbox').css('position', 'fixed');
		      }

			$("#overlay, #lightbox").click(function(){ end(); }).hide();
			$("#loadingLink, #bottomNavClose").click(function(){ end(); return false;});
			$('#outerImageContainer').width(opts.widthCurrent).height(opts.heightCurrent);
			$('#imageDataContainer').width(opts.widthCurrent);
			$('#imageHandlerContainer').width(opts.widthCurrent);

			if (!opts.imageClickClose) {
				$("#lightboxImage").click(function(){ return false; });
				$("#hoverNav").click(function(){ return false; });
			}
			
			return true;
		};

		/*
		# Get the document and window width/heigh
		#
		# Examples
		#
		#	getPageSize()
		#	# => [1024,768,1024,768]
		#
		# Returns a numerically indexed array of document width/height and window width/height
		*/
		function getPageSize() {
			var jqueryPageSize = new Array($(document).width(),$(document).height(), $(window).width(), $(window).height());
			return jqueryPageSize;
		};
	    
		function getPageScroll() {
			var xScroll, yScroll;

			if (self.pageYOffset) {
				yScroll = self.pageYOffset;
				xScroll = self.pageXOffset;
			} else if (document.documentElement && (document.documentElement.scrollTop || document.documentElement.scrollLeft)){  // Explorer 6 Strict, Firefox
				yScroll = document.documentElement.scrollTop;
				xScroll = document.documentElement.scrollLeft;
			} else if (document.body) {// all other Explorers
				yScroll = document.body.scrollTop;
				xScroll = document.body.scrollLeft;
			}

			var arrayPageScroll = new Array(xScroll,yScroll);
			return arrayPageScroll;
		};

		/*
		# Deploy the sexy overlay and display the lightbox
		#
		# imageObject - the jQuery object passed via the click event in the constructor
		#
		# Examples
		#
		#	showLightbox($('#CheesusCrust'))
		#
		# Returns a boolean true, because it's got nothing else to return. It should give visual feedback when run
		*/
		function showLightbox(imageObject) {
			// Resize and display the sexy, sexy overlay.
			resizeOverlayToFitWindow();
			$("#overlay").hide().css({ opacity : opts.overlayOpacity }).fadeIn();
			imageNum = 0;
			
			var data = (imageObject.rel).split("|/|");

			opts.imageArray = [];
			// if image is NOT part of a set..
            if ((!getImageSetOf(imageObject) || (getImageSetOf(imageObject) == '')) && !opts.allSet) {
				// add single image to Lightbox.imageArray
            	
				opts.imageArray.push(new Array(data[1], opts.displayTitle ? data[2] : '', data[3]));
			} else {
				// if image is part of a set..
				$("a").each(function() {
                    if(this.rel && (getImageSetOf(this) == getImageSetOf(imageObject))) {
						opts.imageArray.push(new Array((this.rel).split("|/|")[1], opts.displayTitle ? (this.rel).split("|/|")[2] : '', (this.rel).split("|/|")[3]));
					}
				});
			}
			
			if (opts.imageArray.length > 1) {
				while (opts.imageArray[imageNum][0] != data[1]) { 
					imageNum++;
				}
			}
			// calculate top and left offset for the lightbox
			var arrayPageScroll = getPageScroll();
			var lightboxTop = arrayPageScroll[1];
			var lightboxLeft = arrayPageScroll[0];
			$('#lightbox').css({top: ($(window).height()-400)*0.5 +'px', left: lightboxLeft+'px'}).show();

			if (!opts.slideNavBar) {
				$('#imageData').hide();
			}

			changeImage(imageNum);
		};
	    
		function changeImage(imageNum, flag) {
			if(flag == undefined){
				flag = 0;
				rotateNum = 0;
			}
			
			if (opts.inprogress == false) {
				opts.inprogress = true;

				// update global var
				opts.activeImage = imageNum;	

				// hide elements during transition
				$('#loading').show();
				$('#lightboxImage, #hoverNav, #prevLink, #nextLink').hide();

				// delay preloading image until navbar will slide up
				if (opts.slideNavBar) { 
					$('#imageDataContainer').hide();
					$('#imageData').hide();
				}
				doChangeImage(flag);
			}
		};

		function doChangeImage(flag) {
			var imgPreloader = new Image();
			var _urlGroup = $.fn.lightbox.getPreviewUrl( opts.imageArray[opts.activeImage][0] );
			var _url = _urlGroup.split("|||")[0] +"/thumbnail?minWidth=1000&minHeight=800";
			var _urlError = _urlGroup.split("|||")[1];
			
			// once image is preloaded, resize image container
			imgPreloader.onload = function() {
			    var ua = window.navigator.userAgent;  
			    var isSafari = ua.indexOf("Safari") != -1 && ua.indexOf("Version") != -1; 
				if(isSafari){
					_urlGroup = $.fn.lightbox.getPreviewUrl( opts.imageArray[opts.activeImage][0] );
					_url = _urlGroup.split("|||")[0] +"/thumbnail?minWidth=1000&minHeight=800";
				}
				var newWidth = imgPreloader.width;
				var newHeight = imgPreloader.height;
				
				if( flag == 1 || flag == 3 ){
					newWidth = imgPreloader.height;
					newHeight = imgPreloader.width;
				}

				if (opts.scaleImages) {
					newWidth = parseInt(opts.xScale * newWidth);
					newHeight = parseInt(opts.yScale * newHeight);
				}
				
				if (opts.fitToScreen) {
					var iWidth = imgPreloader.width;
					var iHeight = imgPreloader.height;
					if( flag == 1 || flag == 3 ){
						iWidth = imgPreloader.height;
						iHeight = imgPreloader.width;
					}
					
					var arrayPageSize = getPageSize();
					var initialPageWidth = arrayPageSize[2] - 2 * opts.borderSize - 50;
					var initialPageHeight = arrayPageSize[3] - $("#imageData").outerHeight() - $("#imageHandlerContainer").outerHeight() - 2 * opts.borderSize - 50;

					var dI = initialPageWidth/initialPageHeight;
					var dP = iWidth/iHeight;

					if ((iHeight > initialPageHeight) || (iWidth > initialPageWidth)) {
						if (dI > dP) {
							newHeight = initialPageHeight;
							newWidth = parseInt((initialPageHeight/iHeight) * iWidth);
						} else {
							newWidth = initialPageWidth;
							newHeight = parseInt((initialPageWidth/iWidth) * iHeight);
						}
					}
				}
				if( flag == 1 || flag == 3 ){
					$('#lightboxImage').attr('src', _url).width(newHeight).height(newWidth);
				}else{
					$('#lightboxImage').attr('src', _url).width(newWidth).height(newHeight);
				}
				
				//旋转图像
				if(!(!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0") > 0)){ // 在非ie8、9中执行 
					$("#lightboxImage").css("transform","rotate("+ flag*90 +"deg)");
				}else{
					$("#lightboxImage").css("filter","progid:DXImageTransform.Microsoft.BasicImage(rotation="+ flag +")");
				}
				
				resizeImageContainer(newWidth, newHeight);
			};
			imgPreloader.onerror = function(){
				$('#lightboxImage').attr('src', _urlError).width(200).height(200);
				
				//不旋转图像
				if(!(!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0") > 0)){ // 在非ie8、9中执行 
					$("#lightboxImage").css("transform","rotate(0deg)");
				}else{
					$("#lightboxImage").css("filter","");
				}
				resizeImageContainer(200, 200);
			}

			imgPreloader.src = _url;
		};

		function end() {
			disableKeyboardNav();
			$('#lightbox, #overlay').remove();
		};

		function preloadNeighborImages() {
			var preloadPrevImage, preloadNextImage;
			if (opts.loopImages && opts.imageArray.length > 1) {
				preloadNextImage = new Image();
				preloadNextImage.src = ($.fn.lightbox.getPreviewUrl( opts.imageArray[(opts.activeImage == (opts.imageArray.length - 1)) ? 0 : opts.activeImage + 1][0] )).split("|||")[0];

				preloadPrevImage = new Image();
				preloadPrevImage.src = ($.fn.lightbox.getPreviewUrl( opts.imageArray[(opts.activeImage == 0) ? (opts.imageArray.length - 1) : opts.activeImage - 1][0] )).split("|||")[0];
			} else {
				if ((opts.imageArray.length - 1) > opts.activeImage) {
					preloadNextImage = new Image();
					preloadNextImage.src = ($.fn.lightbox.getPreviewUrl( opts.imageArray[opts.activeImage + 1][0] )).split("|||")[0];
				}
				if (opts.activeImage > 0) {
					preloadPrevImage = new Image();
					preloadPrevImage.src = ($.fn.lightbox.getPreviewUrl( opts.imageArray[opts.activeImage - 1][0] )).split("|||")[0];
				}
			}
		};

		function resizeImageContainer(imgWidth, imgHeight) {
			// get current width and height
			opts.widthCurrent = $("#outerImageContainer").outerWidth();
			opts.heightCurrent = $("#outerImageContainer").outerHeight();

			// get new width and height
			/*var minWidth=350,minHeight=200;
			var widthNew = Math.max(minWidth, imgWidth  + (opts.borderSize * 2));
			var heightNew = Math.max(minHeight, imgHeight  + (opts.borderSize * 2));*/
			var minWidth=600,minHeight=400;
			var widthNew =600;
			var heightNew = 400;
			
			// calculate size difference between new and old image, and resize if necessary
			wDiff = opts.widthCurrent - widthNew;
			hDiff = opts.heightCurrent - heightNew;
			posTop = ($(window).height() - heightNew - $("#imageData").outerHeight() - $("#imageHandlerContainer").outerHeight())*0.5;
			
			$('#imageDataContainer').animate({width: widthNew},opts.resizeSpeed,'linear');
			$('#imageHandlerContainer').animate({width: widthNew},opts.resizeSpeed,'linear');
			$('#outerImageContainer').animate({width: widthNew},opts.resizeSpeed,'linear', function() {
				$('#lightbox').animate({top: posTop},opts.resizeSpeed,'linear');
				$('#outerImageContainer').animate({height: heightNew},opts.resizeSpeed,'linear', function() {
					showImage();
				});
			});
			 
			//小图片居中显示
			if(widthNew == minWidth){
				$('#lightboxImage').css("margin-left",(minWidth - opts.borderSize * 2 - imgWidth)*0.5);
			}else{
				$('#lightboxImage').css("margin-left",0);
			}
			if(heightNew == minHeight){
				$('#lightboxImage').css("margin-top",(minHeight - opts.borderSize * 2 - imgHeight)*0.5);
			}else{
				$('#lightboxImage').css("margin-top",0);
			}
			
			if(!(!$.support.leadingWhitespace || navigator.userAgent.indexOf("MSIE 9.0") > 0)){ // 在非ie8、9中执行 
				if(rotateNum == 1 || rotateNum == 3){
					if(widthNew > minWidth){
						$('#lightboxImage').css("margin-left",(imgWidth - imgHeight)*0.5);
					}else{
						$('#lightboxImage').css("margin-left",(minWidth - opts.borderSize * 2 - imgHeight )*0.5);
					}
					if(heightNew > minHeight){
						$('#lightboxImage').css("margin-top",(imgHeight - imgWidth)*0.5);
					}else{
						$('#lightboxImage').css("margin-top",(minHeight - opts.borderSize * 2 - imgWidth)*0.5);
					}
				}
			}
			
			afterTimeout = function () {
				$('#hoverNav, #prevLink, #nextLink').height(heightNew - (opts.borderSize * 2));
				$('#hoverNav').width(widthNew - (opts.borderSize * 2));
				$('#hoverNav').css({"position":"absolute", "top":opts.borderSize, "left":opts.borderSize, "z-index":9997,});
			};

			// if new and old image are same size and no scaling transition is necessary,
			// do a quick pause to prevent image flicker.
			if((hDiff == 0) && (wDiff == 0)) {
				setTimeout(afterTimeout, 100);
			} else {
			    // otherwise just trigger the height and width change
			    afterTimeout();
			}

		};

		function showImage() {
			$('#loading').hide();
			$('#lightboxImage').fadeIn("fast");
			updateDetails();
			//preloadNeighborImages();  //预加载会引起页面访问缓慢

			opts.inprogress = false;
		};

		function updateDetails() {
			$('#numberDisplay').html('');
			if (opts.imageArray[opts.activeImage][1]) {
				$('#caption').text(opts.imageArray[opts.activeImage][1]).show();
			}

			// if image is part of set display 'Image x of x'
			//if (opts.imageArray.length > 1) {
				var nav_html = (opts.activeImage + 1) + "/" + opts.imageArray.length;
				$('#numberDisplay').html(nav_html).show();
			//}

			if (opts.slideNavBar) {
				$("#imageData").slideDown(opts.navBarSlideSpeed);
			} else {
				$("#imageData").show();
			}

			resizeOverlayToFitWindow();
			updateNav();
		};

		/*
		# Resize the sexy overlay to fit the constraints of your current viewing environment
		# 
		# This should now happen whenever a window is resized, so you should always see a full overlay
		*/
		function resizeOverlayToFitWindow(){
			$('#overlay').css({width: $(window).width(), height: $(document).height()});
			//  ^^^^^^^ <- sexy!
		};

		function updateNav() {
			if (opts.imageArray.length > 1) {
				$('#hoverNav').show();

				// if loopImages is true, always show next and prev image buttons 
				if(opts.loopImages) {
					$('#prevLink, #btnPrev').show().click(function() {
						changeImage((opts.activeImage == 0) ? (opts.imageArray.length - 1) : (opts.activeImage - 1)); 
						return false;
					});

					$('#nextLink, #btnNext').show().click(function() {
						changeImage((opts.activeImage == (opts.imageArray.length - 1)) ? 0 : (opts.activeImage + 1)); 
						return false;
					});

				} else {
					// if not first image in set, display prev image button
					if(opts.activeImage != 0) {
						$('#prevLink, #btnPrev').show().click(function() {
							changeImage(opts.activeImage - 1); 
							return false;
						});
					}else{
						$('#btnPrev').hide();
					}

					// if not last image in set, display next image button
					if(opts.activeImage != (opts.imageArray.length - 1)) {
						$('#nextLink, #btnNext').show().click(function() {
							changeImage(opts.activeImage +1); 
							return false;
						});
					}else{
						$('#btnNext').hide();
					}
				}

			}
			enableKeyboardNav();
			
			$('#rotateR, #rotateL, #btnDownload').unbind("click");
			$('#rotateR').click(function() {
				rotateNum = (rotateNum + 1) % 4; 
				changeImage(opts.activeImage, rotateNum); 
				return false;
			});
			$('#rotateL').click(function() {
				rotateNum = (rotateNum +3) % 4;
				changeImage(opts.activeImage, rotateNum);
				return false;
			});
			$('#btnDownload').click(function(){
				$.fn.lightbox.downloadImg( opts.imageArray[opts.activeImage][2] );
				return false;
			})
		};

		function keyboardAction(e) {
			var o = e.data.opts;
			var keycode = e.keyCode;
			var escapeKey = 27;

			var key = String.fromCharCode(keycode).toLowerCase();

			// close lightbox
			if ((key == 'x') || (key == 'o') || (key == 'c') || (keycode == escapeKey)) { 
				end();

				// display previous image	
			} else if ((key == 'p') || (keycode == 37)) {  
				if(o.loopImages) {
					disableKeyboardNav();
					changeImage((o.activeImage == 0) ? (o.imageArray.length - 1) : o.activeImage - 1);
				} else if (o.activeImage != 0) {
					disableKeyboardNav();
					changeImage(o.activeImage - 1);
				}
				// display next image
			} else if ((key == 'n') || (keycode == 39)) { 
				if (opts.loopImages) {
					disableKeyboardNav();
					changeImage((o.activeImage == (o.imageArray.length - 1)) ? 0 : o.activeImage + 1);
				} else if (o.activeImage != (o.imageArray.length - 1)) {
					disableKeyboardNav();
					changeImage(o.activeImage + 1);
				}
			}
		};

		function enableKeyboardNav() {
			$(document).bind('keydown', {opts: opts}, keyboardAction);
		};

		function disableKeyboardNav() {
			$(document).unbind('keydown');
		};

        function getImageSetOf(imageObject) {
        	var data = (imageObject.rel).split("|/|");
            var set_name = data[0];
            if (!set_name || set_name == '') {
                set_name = $(imageObject).attr('data-lightbox-set');
            }
            return set_name;
        };
	};
	
	/* [outside port]get preview url */
	$.fn.lightbox.getPreviewUrl = function(url){
		
	}
	/* [outside port]download image */
	$.fn.lightbox.downloadImg = function(id){
		
	}
	/* close lightbox */
	$.fn.lightbox.close = function(){
		$(document).unbind('keydown');
		$('#lightbox, #overlay').remove();
	}

	$.fn.lightbox.defaults = {
		allSet: false,
		overlayOpacity: 0.6,
		borderSize: 10,
		imageArray: new Array,
		activeImage: null,
		imageScroll: true,
		inprogress: false,
		resizeSpeed: 300,
		widthCurrent: 450,
		heightCurrent: 350,
		scaleImages: false,
		xScale: 1,
		yScale: 1,
		displayTitle: true,
		slideNavBar: false, 
		navBarSlideSpeed: 300,
		strings: {
			closeTitle: 'close image gallery',
			image: 'Image ',
			rotateR: 'Rotate Clockwise',
			rotateL: 'Rotate Counterclockwise',
			download: 'Download',
			prev: 'Previous',
			next: 'Next',
			autoplay: 'Autoplay/Stop'
		},
		fitToScreen: true,		
		loopImages: false,
		imageClickClose: true
	};	
})(jQuery);

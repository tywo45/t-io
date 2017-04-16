var home_domain = "blog.csdn.net";
var ad_cnt = ad_cnt || 0;

/*CSDN广告延迟加载 t:广告类型，数字；id:容器id*/
var Ad = function (t, id) {
    this.adType = t;
    this.containerId = id;
    this.adIndex = ad_cnt++;

    this.loadAd = function () {
        //document.domain = "csdn.net";

        var container = document.getElementById(this.containerId);
        var width = container.offsetWidth - 2 - container.style.paddingLeft - container.style.paddingRight;
        //var height = container.offsetHeight - 2 - container.style.paddingTop - container.style.paddingBottom;

        var frm = document.createElement('iframe');
        frm.id = "ad_frm_" + this.adIndex;
        frm.frameBorder = "0";
        frm.scrolling = "no";
        frm.style.borderWidth = "0px";
        frm.style.overflow = "hidden";
        if (width > 0) frm.style.width = width + "px";
        else frm.style.width = "98%";
        frm.style.height = "0px";
        frm.src = "http://" + home_domain + "/common/ad.html"
            + "?t=" + this.adType
            + "&containerId=" + this.containerId
            + "&frmId=" + frm.id;
        container.appendChild(frm);
    };
    if (jQuery) {
        jQuery((function (_ad) {
            return function () { _ad.loadAd(); }
        })(this));
    } else {
        this.loadAd();
    }
};
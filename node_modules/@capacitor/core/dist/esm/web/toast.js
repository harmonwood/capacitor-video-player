import * as tslib_1 from "tslib";
import { WebPlugin } from './index';
var ToastPluginWeb = /** @class */ (function (_super) {
    tslib_1.__extends(ToastPluginWeb, _super);
    function ToastPluginWeb() {
        return _super.call(this, {
            name: 'Toast',
            platforms: ['web']
        }) || this;
    }
    ToastPluginWeb.prototype.show = function (options) {
        return tslib_1.__awaiter(this, void 0, void 0, function () {
            var controller, duration, toast;
            return tslib_1.__generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        controller = document.querySelector('ion-toast-controller');
                        if (!controller) {
                            controller = document.createElement('ion-toast-controller');
                            document.body.appendChild(controller);
                        }
                        return [4 /*yield*/, controller.componentOnReady()];
                    case 1:
                        _a.sent();
                        duration = 3000;
                        if (options.duration) {
                            duration = options.duration === 'long' ? 5000 : 3000;
                        }
                        return [4 /*yield*/, controller.create({
                                position: 'bottom',
                                message: options.text,
                                duration: duration,
                            })];
                    case 2:
                        toast = _a.sent();
                        return [4 /*yield*/, toast.present()];
                    case 3: return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    return ToastPluginWeb;
}(WebPlugin));
export { ToastPluginWeb };
var Toast = new ToastPluginWeb();
export { Toast };
//# sourceMappingURL=toast.js.map
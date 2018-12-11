import * as tslib_1 from "tslib";
import { WebPlugin } from './index';
//import '@ionic/pwa-elements';
var CameraPluginWeb = /** @class */ (function (_super) {
    tslib_1.__extends(CameraPluginWeb, _super);
    function CameraPluginWeb() {
        return _super.call(this, {
            name: 'Camera',
            platforms: ['web']
        }) || this;
    }
    CameraPluginWeb.prototype.getPhoto = function (options) {
        return tslib_1.__awaiter(this, void 0, void 0, function () {
            var _this = this;
            return tslib_1.__generator(this, function (_a) {
                options;
                return [2 /*return*/, new Promise(function (resolve, reject) { return tslib_1.__awaiter(_this, void 0, void 0, function () {
                        var _this = this;
                        var cameraModal;
                        return tslib_1.__generator(this, function (_a) {
                            switch (_a.label) {
                                case 0:
                                    cameraModal = document.createElement('ion-pwa-camera-modal');
                                    document.body.appendChild(cameraModal);
                                    return [4 /*yield*/, cameraModal.componentOnReady()];
                                case 1:
                                    _a.sent();
                                    cameraModal.addEventListener('onPhoto', function (e) { return tslib_1.__awaiter(_this, void 0, void 0, function () {
                                        var photo, _a;
                                        return tslib_1.__generator(this, function (_b) {
                                            switch (_b.label) {
                                                case 0:
                                                    photo = e.detail;
                                                    if (!(photo === null)) return [3 /*break*/, 1];
                                                    reject();
                                                    return [3 /*break*/, 3];
                                                case 1:
                                                    _a = resolve;
                                                    return [4 /*yield*/, this._getCameraPhoto(photo)];
                                                case 2:
                                                    _a.apply(void 0, [_b.sent()]);
                                                    _b.label = 3;
                                                case 3:
                                                    cameraModal.dismiss();
                                                    return [2 /*return*/];
                                            }
                                        });
                                    }); });
                                    cameraModal.present();
                                    return [2 /*return*/];
                            }
                        });
                    }); })];
            });
        });
    };
    CameraPluginWeb.prototype._getCameraPhoto = function (photo) {
        return new Promise(function (resolve, reject) {
            var reader = new FileReader();
            reader.readAsDataURL(photo);
            reader.onloadend = function () {
                resolve({
                    base64Data: reader.result,
                    webPath: reader.result,
                    format: 'jpeg'
                });
            };
            reader.onerror = function (e) {
                reject(e);
            };
        });
    };
    return CameraPluginWeb;
}(WebPlugin));
export { CameraPluginWeb };
var Camera = new CameraPluginWeb();
export { Camera };
//# sourceMappingURL=camera.js.map
import { CapacitorWeb } from './web-runtime';
// Create our default Capacitor instance, which will be
// overridden on native platforms
var Capacitor = new CapacitorWeb();
Capacitor = window.Capacitor || Capacitor;
// Export window.Capacitor if not available already (ex: web)
if (!window.Capacitor) {
    window.Capacitor = Capacitor;
}
var Plugins = Capacitor.Plugins;
export { Capacitor, Plugins };
//# sourceMappingURL=global.js.map
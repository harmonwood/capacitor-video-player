import { WebPlugin } from './index';
import { DeviceInfo, DevicePlugin } from '../core-plugin-definitions';
export declare class DevicePluginWeb extends WebPlugin implements DevicePlugin {
    constructor();
    getInfo(): Promise<DeviceInfo>;
    parseUa(_ua: string): any;
    getUid(): string;
}
declare const Device: DevicePluginWeb;
export { Device };

export declare class CapacitorWeb {
    Plugins: {};
    platform: string;
    isNative: boolean;
    constructor();
    pluginMethodNoop(_target: any, _prop: PropertyKey, pluginName: string): Promise<never>;
    getPlatform(): string;
    isPluginAvailable(name: string): boolean;
    handleError(e: Error): void;
}

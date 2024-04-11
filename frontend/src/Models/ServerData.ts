import {ServerType} from "./ServerType";
import {ServerStatus} from "./ServerStatus";
import {ServerResources} from "./ServerResources";

export interface ServerData {
    id: string,
    name: string,
    type: ServerType,
    ram: string,
    disk: string,
    path: string,
    version: string,
    status: ServerStatus,
    port: number,
    networkServers: string[],
    serverResources: ServerResources[]
}
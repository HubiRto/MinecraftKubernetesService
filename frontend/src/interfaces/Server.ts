import {ServerType} from "./ServerType.ts";
import {ServerStatus} from "./ServerStatus.ts";

export interface Server {
    id: string,
    name: string,
    type: ServerType,
    ram: string,
    disk: string,
    path: string,
    version: string,
    status: ServerStatus,
    port: number,
    ipAddress: string,
    rconPort: number
}

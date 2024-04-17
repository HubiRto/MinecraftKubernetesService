import {ServerStatus} from "../enums/server-status";
import {ServerType} from "../enums/server-type";

export interface Server {
  id: number,
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

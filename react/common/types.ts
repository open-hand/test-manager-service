export interface IUser {
  email: string
  enabled?: boolean
  id: number
  imageUrl: string | null
  ldap: boolean
  loginName: string
  realName: string
}
export interface IIssueType {
  colour: string,
  description: string,
  icon: string,
  id: string,
  name: string,
  stateMachineId: string,
  typeCode: string,
  enabled: boolean
}
export type IFeatureType = 'business' | 'enabler'

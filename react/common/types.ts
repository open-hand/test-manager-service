export interface IUser {
  email: string
  enabled?: boolean
  id: number
  imageUrl: string | null
  ldap: boolean
  loginName: string
  realName: string
}

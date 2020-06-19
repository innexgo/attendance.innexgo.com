declare module '*.png'

enum UserRing {
  Administrator = 0,
  Teacher = 1,
}

type User = {
  id: number,
  name: string,
  email:string,
  ring:UserRing,
}

type ApiKey = {
  id:number,
  creationTime:number,
  expirationTime:number,
  key:string,
  user:User,
}

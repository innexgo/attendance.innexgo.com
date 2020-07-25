declare module '*.png'

type Location = {
	id: number,
	name: string
}

type Course = {
  id:number,
	teacher:User
	location:Location,
	period:number,
	subject:string
}

enum PeriodKind {
  PASSING,
  CLASS,
  BREAK,
  LUNCH,
  TUTORIAL,
  NONE,
}

type Period = {
	startTime:number,
	numbering:number
	kind:PeriodKind
}

type UserPreferences = {
	defaultLocation:Location
}

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

interface AuthenticatedComponentProps {
	apiKey: ApiKey
  setApiKey: (data:ApiKey|null)=>void
}

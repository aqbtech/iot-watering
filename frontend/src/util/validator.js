export const FIELD_REQUIRED_MESSAGE = 'This field is required.'
export const USERNAME_RULE = /^.{8,20}$/ // Username từ 8-20 ký tự
export const USERNAME_RULE_MESSAGE =
  'Username must be between 8 and 20 characters.'
export const PASSWORD_RULE = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d\W]{8,256}$/
export const PASSWORD_RULE_MESSAGE =
  'Password must include at least 1 letter, a number, and at least 8 characters.'

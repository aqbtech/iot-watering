import React from 'react'
import { assets } from '../../assets/asset'
import RegisterForm from './RegisterForm.jsx'

const Register = () => {
  return (
    <>
      <div className="flex flex-row min-h-screen">
        <div className="flex-1 flex items-center justify-center bg-blue-50 p-4">
          <RegisterForm />
        </div>
        <div className="flex-1 flex items-center justify-center">
          <img
            src={assets.backgroundAuth}
            alt="background"
            className="rounded-l-lg"
          />
        </div>
      </div>
    </>
  )
}
export default Register
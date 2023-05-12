import { Avatar, IconButton, Typography } from '@mui/material';
import Tooltip from '@mui/material/Tooltip';
import { useEffect, useState } from 'react';


const UserProfile = () => {

  const [userInfo, setUserInfo] = useState({ name: "", avatarUrl: ""})

  useEffect(() => {
    fetch("/api/user/info")
      .then(res => res.json())
      .then(body => {
        if (body.code === 0) {
          const data = body.data
          setUserInfo({ name: data.name, avatarUrl: data.avatarUrl})
        } else {
          console.log(body)
        }
      }, err => {
        console.err(err)
      })
  })

  return (
    <Tooltip
      title={ <Typography>{ userInfo.name }</Typography> }
    >
      <IconButton>
        <Avatar
          alt="name"
          src={ userInfo.avatarUrl }
          sx={ { width: 32, height: 32 } }
        />
      </IconButton>
    </Tooltip>
  )
}

export default UserProfile
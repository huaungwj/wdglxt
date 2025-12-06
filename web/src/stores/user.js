import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '')
    const username = ref(localStorage.getItem('username') || '')
    const realName = ref(localStorage.getItem('realName') || '')

    function setUser(userToken, userName, userRealName) {
        token.value = userToken
        username.value = userName
        realName.value = userRealName

        localStorage.setItem('token', userToken)
        localStorage.setItem('username', userName)
        localStorage.setItem('realName', userRealName)
    }

    function clearUser() {
        token.value = ''
        username.value = ''
        realName.value = ''

        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('realName')
    }

    const isLoggedIn = () => !!token.value

    return {
        token,
        username,
        realName,
        setUser,
        clearUser,
        isLoggedIn
    }
})

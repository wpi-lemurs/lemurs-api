import { useState } from "react";

export default function useToken() {
    const getToken = () => {
        const tokenString = localStorage.getItem("token");
        return tokenString || "";
    };

    const [token, setToken] = useState(getToken());

    const saveToken = (userToken) => {
        if (userToken) {
            localStorage.setItem("token", userToken);
        } else {
            localStorage.removeItem("token");
        }
        setToken(userToken || "");
    };

    return {
        token,
        setToken: saveToken,
    };
}
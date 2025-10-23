import { useState } from "react";

export default function useToken() {
    const getToken = () => {
        const tokenString = localStorage.getItem("token");
        if (tokenString == null) {
            return "";
        }
        const tokenJson = JSON.parse(tokenString);
        return tokenJson;
    };

    const [token, setToken] = useState(getToken());

    const saveToken = (userToken) => {
        localStorage.setItem("token", JSON.stringify(userToken));
        setToken(userToken);
    };

    return {
        token,
        setToken: saveToken,
    };
}
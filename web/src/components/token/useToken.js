import { useMsal, useAccount } from "@azure/msal-react";
import { useEffect, useState } from "react";
import { loginRequest } from "../../authConfig";

export default function useToken() {
  const { instance, accounts } = useMsal();
  const account = useAccount(accounts[0] || {});
  const [token, setToken] = useState("");

  useEffect(() => {
    if (account) {
      instance
        .acquireTokenSilent({
          ...loginRequest,
          account: account,
        })
        .then((response) => {
          setToken(response.accessToken);
        })
        .catch(() => {
          setToken("");
        });
    } else {
      setToken("");
    }
  }, [account, instance]);

  return { token, setToken };
}

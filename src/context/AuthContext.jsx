import { createContext, useState, useEffect } from "react";
import { getMyProfile, login as apiLogin, logout as apiLogout } from "../services/api";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getMyProfile()
            .then(setUser)
            .catch(() => setUser(null))
            .finally(() => setLoading(false));
    }, []);

    async function login(username, password) {
        await apiLogin(username, password);
        const profile = await getMyProfile();
        setUser(profile);
    }

    async function logout() {
        await apiLogout();
        setUser(null);
    }

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
}
import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api, { attachToken } from '../api/client';

const AuthContext = createContext(null);
const STORAGE_KEY = 'papertrail_auth';

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const bootstrap = async () => {
      try {
        const raw = await AsyncStorage.getItem(STORAGE_KEY);
        if (raw) {
          const parsed = JSON.parse(raw);
          setToken(parsed.token);
          setUser(parsed.user);
          attachToken(parsed.token);
        }
      } finally {
        setLoading(false);
      }
    };
    bootstrap();
  }, []);

  const persist = async (nextToken, nextUser) => {
    setToken(nextToken);
    setUser(nextUser);
    attachToken(nextToken);
    await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ token: nextToken, user: nextUser }));
  };

  const register = async (payload) => {
    const { data } = await api.post('/auth/register', payload);
    await persist(data.token, data.user);
    return data;
  };

  const login = async (identifier, password) => {
    const { data } = await api.post('/auth/login', { identifier, password });
    await persist(data.token, data.user);
    return data;
  };

  const refreshProfile = async () => {
    const { data } = await api.get('/auth/profile');
    setUser(data);
    await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ token, user: data }));
    return data;
  };

  const logout = async () => {
    setToken(null);
    setUser(null);
    attachToken(null);
    await AsyncStorage.removeItem(STORAGE_KEY);
  };

  const value = useMemo(
    () => ({ token, user, loading, register, login, refreshProfile, logout }),
    [token, user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}

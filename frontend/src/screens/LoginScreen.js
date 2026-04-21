import React, { useState } from 'react';
import { Alert, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { useAuth } from '../context/AuthContext';
import errorMessage from '../utils/errorMessage';

export default function LoginScreen({ navigation }) {
  const { login } = useAuth();
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [busy, setBusy] = useState(false);

  const onLogin = async () => {
    if (!identifier || !password) {
      Alert.alert('Validation', 'Enter email/mobile and password.');
      return;
    }

    setBusy(true);
    try {
      await login(identifier, password);
    } catch (error) {
      Alert.alert('Login Failed', errorMessage(error));
    } finally {
      setBusy(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>Welcome to PaperTrail</Text>
      <TextInput
        style={styles.input}
        placeholder="Email or mobile"
        value={identifier}
        onChangeText={setIdentifier}
        autoCapitalize="none"
      />
      <TextInput
        style={styles.input}
        placeholder="Password"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />
      <TouchableOpacity style={styles.button} onPress={onLogin} disabled={busy}>
        <Text style={styles.buttonText}>{busy ? 'Logging in...' : 'Login'}</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate('Register')}>
        <Text style={styles.link}>New user? Register</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 24, backgroundColor: '#f2f6fb' },
  heading: { fontSize: 26, fontWeight: '700', marginBottom: 24, color: '#1e3a5f' },
  input: {
    backgroundColor: 'white',
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#d8e2ef',
    padding: 12,
    marginBottom: 12
  },
  button: {
    backgroundColor: '#145bba',
    borderRadius: 10,
    padding: 14,
    alignItems: 'center',
    marginTop: 8
  },
  buttonText: { color: 'white', fontWeight: '600' },
  link: { color: '#145bba', textAlign: 'center', marginTop: 14, fontWeight: '500' }
});

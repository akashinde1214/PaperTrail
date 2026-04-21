import React, { useState } from 'react';
import { Alert, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { useAuth } from '../context/AuthContext';
import errorMessage from '../utils/errorMessage';

export default function RegisterScreen() {
  const { register } = useAuth();
  const [form, setForm] = useState({
    name: '',
    mobile: '',
    email: '',
    password: '',
    address: '',
    age: ''
  });
  const [busy, setBusy] = useState(false);

  const update = (key, value) => setForm((prev) => ({ ...prev, [key]: value }));

  const onSubmit = async () => {
    if (!form.name || !form.mobile || !form.email || !form.password || !form.address || !form.age) {
      Alert.alert('Validation', 'Please fill all fields.');
      return;
    }

    const ageNum = Number(form.age);
    if (!Number.isInteger(ageNum) || ageNum < 18) {
      Alert.alert('Validation', 'Age must be 18 or above.');
      return;
    }

    setBusy(true);
    try {
      await register({
        name: form.name,
        mobile: form.mobile,
        email: form.email,
        password: form.password,
        address: form.address,
        age: ageNum
      });
      Alert.alert('Success', 'Registration completed.');
    } catch (error) {
      Alert.alert('Registration Failed', errorMessage(error));
    } finally {
      setBusy(false);
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <Text style={styles.heading}>Create Account</Text>

      <TextInput style={styles.input} placeholder="Name" value={form.name} onChangeText={(v) => update('name', v)} />
      <TextInput
        style={styles.input}
        placeholder="Mobile (10 digits)"
        value={form.mobile}
        onChangeText={(v) => update('mobile', v)}
        keyboardType="phone-pad"
      />
      <TextInput
        style={styles.input}
        placeholder="Email"
        value={form.email}
        onChangeText={(v) => update('email', v)}
        autoCapitalize="none"
      />
      <TextInput
        style={styles.input}
        placeholder="Password"
        value={form.password}
        onChangeText={(v) => update('password', v)}
        secureTextEntry
      />
      <TextInput
        style={[styles.input, styles.multiline]}
        placeholder="Address"
        value={form.address}
        onChangeText={(v) => update('address', v)}
        multiline
      />
      <TextInput
        style={styles.input}
        placeholder="Age"
        value={form.age}
        onChangeText={(v) => update('age', v)}
        keyboardType="number-pad"
      />

      <TouchableOpacity style={styles.button} onPress={onSubmit} disabled={busy}>
        <Text style={styles.buttonText}>{busy ? 'Registering...' : 'Register'}</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f7fafc' },
  content: { padding: 24 },
  heading: { fontSize: 26, fontWeight: '700', color: '#0e2b4f', marginBottom: 20 },
  input: {
    backgroundColor: 'white',
    borderColor: '#d7e2ef',
    borderWidth: 1,
    borderRadius: 10,
    padding: 12,
    marginBottom: 12
  },
  multiline: { minHeight: 80, textAlignVertical: 'top' },
  button: {
    backgroundColor: '#0d7a5f',
    borderRadius: 10,
    padding: 14,
    alignItems: 'center',
    marginTop: 4
  },
  buttonText: { color: 'white', fontWeight: '600' }
});

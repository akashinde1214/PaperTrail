import React, { useEffect, useState } from 'react';
import { Alert, RefreshControl, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';
import errorMessage from '../utils/errorMessage';

export default function ProfileScreen() {
  const { user, refreshProfile, logout } = useAuth();
  const [summary, setSummary] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  const loadSummary = async () => {
    const { data } = await api.get('/documents/alerts/summary');
    setSummary(data);
  };

  const onRefresh = async () => {
    setRefreshing(true);
    try {
      await refreshProfile();
      await loadSummary();
    } catch (error) {
      Alert.alert('Error', errorMessage(error));
    } finally {
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadSummary().catch(() => {});
  }, []);

  return (
    <ScrollView
      style={styles.container}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
    >
      <View style={styles.card}>
        <Text style={styles.title}>My Profile</Text>
        <Field label="Name" value={user?.name} />
        <Field label="Mobile" value={user?.mobile} />
        <Field label="Email" value={user?.email} />
        <Field label="Address" value={user?.address} />
        <Field label="Age" value={String(user?.age || '')} />
        <Field label="Verified" value={user?.verified ? 'Yes' : 'No'} />
      </View>

      <View style={styles.card}>
        <Text style={styles.title}>Alert Snapshot</Text>
        <Field label="Total Documents" value={String(summary?.total ?? 0)} />
        <Field label="Expired" value={String(summary?.expired ?? 0)} />
        <Field label="Critical (<=30d)" value={String(summary?.critical ?? 0)} />
        <Field label="Warning (<=90d)" value={String(summary?.warning ?? 0)} />
        <Field label="Safe" value={String(summary?.safe ?? 0)} />
      </View>

      <TouchableOpacity style={styles.logoutButton} onPress={logout}>
        <Text style={styles.logoutText}>Logout</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

function Field({ label, value }) {
  return (
    <View style={styles.row}>
      <Text style={styles.label}>{label}</Text>
      <Text style={styles.value}>{value || '-'}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#eef4f9', padding: 16 },
  card: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 16,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: '#dbe5f1'
  },
  title: { fontSize: 20, fontWeight: '700', marginBottom: 10, color: '#1e3856' },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    borderBottomWidth: 1,
    borderBottomColor: '#edf2f7',
    paddingVertical: 8
  },
  label: { color: '#4b5f76', fontWeight: '500' },
  value: { color: '#1a2b3f', maxWidth: '58%', textAlign: 'right' },
  logoutButton: {
    backgroundColor: '#cc2f2f',
    padding: 14,
    borderRadius: 10,
    alignItems: 'center',
    marginBottom: 20
  },
  logoutText: { color: 'white', fontWeight: '700' }
});

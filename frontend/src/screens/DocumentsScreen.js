import React, { useEffect, useState } from 'react';
import {
  Alert,
  FlatList,
  RefreshControl,
  StyleSheet,
  Text,
  TouchableOpacity,
  View
} from 'react-native';
import api from '../api/client';
import errorMessage from '../utils/errorMessage';

const colorByAlert = {
  EXPIRED: '#c53030',
  CRITICAL: '#dd6b20',
  WARNING: '#d69e2e',
  SAFE: '#2f855a'
};

export default function DocumentsScreen() {
  const [documents, setDocuments] = useState([]);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    const { data } = await api.get('/documents');
    setDocuments(data);
  };

  const remove = async (id) => {
    try {
      await api.delete(`/documents/${id}`);
      await load();
    } catch (error) {
      Alert.alert('Delete Failed', errorMessage(error));
    }
  };

  const onDeletePress = (id) => {
    Alert.alert('Delete Document', 'Are you sure you want to delete this document?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Delete', style: 'destructive', onPress: () => remove(id) }
    ]);
  };

  const onRefresh = async () => {
    setRefreshing(true);
    try {
      await load();
    } catch (error) {
      Alert.alert('Error', errorMessage(error));
    } finally {
      setRefreshing(false);
    }
  };

  useEffect(() => {
    load().catch((e) => Alert.alert('Error', errorMessage(e)));
  }, []);

  return (
    <View style={styles.container}>
      <FlatList
        data={documents}
        keyExtractor={(item) => String(item.id)}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        ListEmptyComponent={<Text style={styles.empty}>No documents added yet.</Text>}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View style={styles.rowSpace}>
              <Text style={styles.name}>{item.name}</Text>
              <Text style={[styles.alert, { color: colorByAlert[item.alertLevel] || '#1a202c' }]}>
                {item.alertLevel}
              </Text>
            </View>
            <Text style={styles.meta}>Type: {item.docType}</Text>
            <Text style={styles.meta}>Number: {item.documentNumber}</Text>
            <Text style={styles.meta}>Expiry: {item.expiryDate}</Text>
            <Text style={styles.meta}>Days Left: {item.daysToExpiry}</Text>
            <TouchableOpacity style={styles.delete} onPress={() => onDeletePress(item.id)}>
              <Text style={styles.deleteText}>Delete</Text>
            </TouchableOpacity>
          </View>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f4f8fd', padding: 12 },
  card: {
    backgroundColor: 'white',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#dce6f1',
    padding: 14,
    marginBottom: 10
  },
  rowSpace: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  name: { fontSize: 17, fontWeight: '700', color: '#183455' },
  alert: { fontSize: 12, fontWeight: '700' },
  meta: { color: '#4d6076', marginTop: 3 },
  delete: {
    marginTop: 10,
    alignSelf: 'flex-start',
    paddingVertical: 6,
    paddingHorizontal: 10,
    borderRadius: 8,
    backgroundColor: '#c53030'
  },
  deleteText: { color: 'white', fontWeight: '600' },
  empty: { textAlign: 'center', marginTop: 40, color: '#5e728a' }
});

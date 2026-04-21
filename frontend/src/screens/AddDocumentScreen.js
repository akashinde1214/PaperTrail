import React, { useState } from 'react';
import { Alert, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import api from '../api/client';
import errorMessage from '../utils/errorMessage';

const DOC_TYPES = [
  'AADHAAR',
  'PASSPORT',
  'PAN',
  'VEHICLE_INSURANCE',
  'RATION_CARD',
  'DRIVING_LICENSE',
  'VOTER_ID',
  'VEHICLE_PUC',
  'HEALTH_INSURANCE',
  'OTHER'
];

export default function AddDocumentScreen() {
  const [form, setForm] = useState({
    docType: 'AADHAAR',
    name: '',
    documentNumber: '',
    issueDate: '',
    expiryDate: '',
    renewalCycleDays: '',
    notes: ''
  });
  const [busy, setBusy] = useState(false);

  const update = (key, value) => setForm((prev) => ({ ...prev, [key]: value }));

  const onSubmit = async () => {
    if (!form.name || !form.documentNumber || !form.issueDate) {
      Alert.alert('Validation', 'Name, document number, and issue date are required.');
      return;
    }

    setBusy(true);
    try {
      await api.post('/documents', {
        docType: form.docType,
        name: form.name,
        documentNumber: form.documentNumber,
        issueDate: form.issueDate,
        expiryDate: form.expiryDate || null,
        renewalCycleDays: form.renewalCycleDays ? Number(form.renewalCycleDays) : null,
        notes: form.notes
      });
      Alert.alert('Success', 'Document saved successfully.');
      setForm({
        docType: 'AADHAAR',
        name: '',
        documentNumber: '',
        issueDate: '',
        expiryDate: '',
        renewalCycleDays: '',
        notes: ''
      });
    } catch (error) {
      Alert.alert('Save Failed', errorMessage(error));
    } finally {
      setBusy(false);
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <Text style={styles.heading}>Add Government Document</Text>

      <Text style={styles.label}>Document Type</Text>
      <View style={styles.typesWrap}>
        {DOC_TYPES.map((type) => (
          <TouchableOpacity
            key={type}
            style={[styles.typeChip, form.docType === type && styles.typeChipActive]}
            onPress={() => update('docType', type)}
          >
            <Text style={[styles.typeChipText, form.docType === type && styles.typeChipTextActive]}>{type}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <TextInput style={styles.input} placeholder="Document Name" value={form.name} onChangeText={(v) => update('name', v)} />
      <TextInput
        style={styles.input}
        placeholder="Document Number"
        value={form.documentNumber}
        onChangeText={(v) => update('documentNumber', v)}
      />
      <TextInput
        style={styles.input}
        placeholder="Issue Date (YYYY-MM-DD)"
        value={form.issueDate}
        onChangeText={(v) => update('issueDate', v)}
      />
      <TextInput
        style={styles.input}
        placeholder="Expiry Date (YYYY-MM-DD) optional"
        value={form.expiryDate}
        onChangeText={(v) => update('expiryDate', v)}
      />
      <TextInput
        style={styles.input}
        placeholder="Renewal Cycle Days optional"
        value={form.renewalCycleDays}
        onChangeText={(v) => update('renewalCycleDays', v)}
        keyboardType="number-pad"
      />
      <TextInput
        style={[styles.input, styles.multiline]}
        placeholder="Notes"
        value={form.notes}
        onChangeText={(v) => update('notes', v)}
        multiline
      />

      <TouchableOpacity style={styles.button} onPress={onSubmit} disabled={busy}>
        <Text style={styles.buttonText}>{busy ? 'Saving...' : 'Save Document'}</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f8fbff' },
  content: { padding: 18 },
  heading: { fontSize: 24, fontWeight: '700', color: '#13365f', marginBottom: 12 },
  label: { fontWeight: '700', marginBottom: 8, color: '#2b4666' },
  typesWrap: { flexDirection: 'row', flexWrap: 'wrap', marginBottom: 12 },
  typeChip: {
    borderWidth: 1,
    borderColor: '#c5d5e6',
    borderRadius: 999,
    paddingVertical: 6,
    paddingHorizontal: 10,
    marginRight: 8,
    marginBottom: 8,
    backgroundColor: '#ffffff'
  },
  typeChipActive: { backgroundColor: '#1256a6', borderColor: '#1256a6' },
  typeChipText: { color: '#2f4e72', fontWeight: '600', fontSize: 12 },
  typeChipTextActive: { color: 'white' },
  input: {
    backgroundColor: 'white',
    borderWidth: 1,
    borderColor: '#d6e2ee',
    borderRadius: 10,
    padding: 12,
    marginBottom: 10
  },
  multiline: { minHeight: 70, textAlignVertical: 'top' },
  button: {
    marginTop: 4,
    backgroundColor: '#0f8b6d',
    borderRadius: 10,
    padding: 14,
    alignItems: 'center'
  },
  buttonText: { color: 'white', fontWeight: '700' }
});

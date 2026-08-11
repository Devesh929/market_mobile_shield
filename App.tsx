import React, { useState } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  NativeModules,
} from 'react-native';

const { OverlayModule } = NativeModules;

function App(): React.JSX.Element {
  const [overlayActive, setOverlayActive] = useState(false);
  const [permissionGranted, setPermissionGranted] = useState(false);

  const requestPermission = async () => {
    try {
      const result = await OverlayModule.requestOverlayPermission();
      setPermissionGranted(result);
    } catch (e) {
      console.error(e);
    }
  };

  const toggleOverlay = () => {
    if (overlayActive) {
      OverlayModule.stopOverlay();
    } else {
      OverlayModule.startOverlay();
    }
    setOverlayActive(!overlayActive);
  };

  const resetDemo = () => {
    OverlayModule.resetDemoCounter();
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Market Shield Mobile Demo</Text>
        <Text style={styles.subtitle}>Agentic Overlay Engine</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>1. Setup Permissions</Text>
        <Text style={styles.cardBody}>
          To draw over YouTube, Instagram, or Telegram, the app requires the 'Display over other apps' permission.
        </Text>
        <TouchableOpacity style={styles.button} onPress={requestPermission}>
          <Text style={styles.buttonText}>1. Request Permission</Text>
        </TouchableOpacity>
        {permissionGranted && <Text style={styles.success}>Permission Granted!</Text>}
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>2. Scenario Demo</Text>
        <Text style={styles.cardBody}>
          Start the overlay to see the floating Market Shield bubble. 
          When you switch to another app, the bubble will remain visible.
        </Text>
        <TouchableOpacity 
          style={[styles.button, overlayActive ? styles.buttonStop : null]} 
          onPress={toggleOverlay}
        >
          <Text style={styles.buttonText}>
            {overlayActive ? "Stop Overlay" : "Start Overlay"}
          </Text>
        </TouchableOpacity>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>3. Reset Presentation</Text>
        <Text style={styles.cardBody}>
          Click this before each demo presentation to reset the sequence back to Step 1 (YouTube Long).
        </Text>
        <TouchableOpacity 
          style={[styles.button, { backgroundColor: '#F59E0B' }]} 
          onPress={resetDemo}
        >
          <Text style={styles.buttonText}>Reset Demo Counter</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F7FAFC',
    padding: 16,
  },
  header: {
    marginVertical: 24,
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#00A7A7',
  },
  subtitle: {
    fontSize: 16,
    color: '#64748B',
    marginTop: 4,
  },
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#0B1F33',
    marginBottom: 8,
  },
  cardBody: {
    fontSize: 14,
    color: '#64748B',
    marginBottom: 16,
    lineHeight: 20,
  },
  button: {
    backgroundColor: '#00A7A7',
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  buttonStop: {
    backgroundColor: '#DC2626',
  },
  buttonText: {
    color: '#FFFFFF',
    fontWeight: '600',
    fontSize: 16,
  },
  success: {
    color: '#059669',
    marginTop: 8,
    fontWeight: '500',
  }
});

export default App;

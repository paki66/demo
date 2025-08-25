import {useEffect, useState} from 'react'
import './App.css'
import {requestNotificationPermission} from './requestNotificationPermission';
import axios from "axios";

function App() {
  const [subscribed, setSubscribed] = useState<boolean>(false)
  const [permission, setPermission] = useState<string>('')

  async function subscribe() {
    const publicKey = await axios.get(`${import.meta.env.VITE_APP_PATH}/public-key`)
    console.log('publicKey', publicKey.data)
    const registration = await navigator.serviceWorker.getRegistration();
    const subscription = await registration?.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: urlB64ToUint8Array(publicKey.data),
    });

    if (subscription) {
      setSubscribed(true)
      console.log('subscription', subscription)
      await axios.post(`${import.meta.env.VITE_APP_PATH}/`, subscription)
    } else {
      setSubscribed(false)
    }
  }

  function urlB64ToUint8Array(base64String: string) {
    const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding)
      .replace(/\-/g, "+")
      .replace(/_/g, "/");
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }

  useEffect(() => {

  }, [])

  return (
    <>
      <button onClick={() => {
        requestNotificationPermission().then(hasPermission => {
          setPermission(hasPermission)
          if (hasPermission) {
            console.log('Notification permission granted');
            subscribe();
          }
        });
      }}>{subscribed.toString()} + {permission}</button>
    </>
  )
}

export default App

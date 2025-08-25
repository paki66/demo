export const requestNotificationPermission = async () => {
  if ('Notification' in window) {
    try {
      return await Notification.requestPermission();
    } catch (error) {
      console.error('Error requesting notification permission:', error);
      return "denied";
    }
  }
  return "unsupported";
};
const register = () => {
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      console.log('Registering service worker...');
      navigator.serviceWorker.register('/service-worker.js')
        .then(registration => {
          console.log('ServiceWorker registration successful');
          return registration;
        })
        .catch(err => {
          console.log('ServiceWorker registration failed: ', err);
        });
    });
  }
};

register();


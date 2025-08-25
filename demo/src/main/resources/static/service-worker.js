self.addEventListener("push", (event) => {
  console.log('push event occurred')
  const payload = event.data?.text() ?? "no payload";
    console.log('body', payload)
  event.waitUntil(
    self.registration.showNotification("ServiceWorker Cookbook", {
      body: payload,
    }),
  );
});

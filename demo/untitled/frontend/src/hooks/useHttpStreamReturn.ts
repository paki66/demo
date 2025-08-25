import {useState, useEffect, useCallback, useRef} from 'react';

interface UseHttpStreamReturn<T> {
  data: T[];
  error: string | null;
  startStream: () => Promise<void>;
  stopStream: () => void;
}

export const useHttpStream = <T = any>(
  url: string,
  options: RequestInit = {}
): UseHttpStreamReturn<T> => {
  const [data, setData] = useState<T[]>([]);
  const [error, setError] = useState<string | null>(null);
  const abortControllerRef = useRef<AbortController | null>(null);

  const startStream = useCallback(async () => {
    try {
      setError(null);
      abortControllerRef.current = new AbortController();

      const response = await fetch(url, {
        ...options,
        signal: abortControllerRef.current.signal,
        headers: {
          'Accept': 'application/x-ndjson',
          // 'Content-Type': 'application/json',
          ...options.headers,
        },
      });

      if (!response.status || response.status < 200 || response.status >= 300) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      if (!response.body) {
        throw new Error('ReadableStream not supported');
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder();

      while (true) {
        const {done, value} = await reader.read();

        if (done) {
          break;
        }

        const chunk = decoder.decode(value);
        const lines = chunk.split('\n').filter(line => line.trim());

        for (const line of lines) {
          try {
            let parsedData = JSON.parse(line);
            setData(prev => [...prev, parsedData]);
          } catch (parseError) {
            console.warn('Failed to parse chunk:', line, parseError);
          }
        }
      }

    } catch (err) {
      if (err instanceof Error && err.name !== 'AbortError') {
        setError(err.message);
        console.log('Error in useHttpStream:', err);
      }
    }
  }, [url, options]);

  const stopStream = useCallback(() => {
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
  }, []);

  useEffect(() => {
    return () => {
      stopStream();
    };
  }, [stopStream]);

  return {
    data,
    error,
    startStream,
    stopStream,
  };
};
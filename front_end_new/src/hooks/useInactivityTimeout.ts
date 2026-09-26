import { useEffect, useRef, useCallback } from 'react';

interface UseInactivityTimeoutOptions {
  timeoutMs: number;    // Tempo total de inatividade até logout (ex: 10 * 60 * 1000)
  warningMs: number;    // Quanto antes do timeout mostrar o aviso (ex: 2 * 60 * 1000)
  onWarning: () => void;
  onTimeout: () => void;
  enabled: boolean;     // Só ativa quando o usuário está autenticado
}

const ACTIVITY_EVENTS: string[] = [
  'mousemove',
  'mousedown',
  'keydown',
  'touchstart',
  'scroll',
  'click',
];

export function useInactivityTimeout({
  timeoutMs,
  warningMs,
  onWarning,
  onTimeout,
  enabled,
}: UseInactivityTimeoutOptions): void {
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const warningRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const warnedRef  = useRef<boolean>(false);

  const clearTimers = useCallback(() => {
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    if (warningRef.current) clearTimeout(warningRef.current);
  }, []);

  const resetTimers = useCallback(() => {
    clearTimers();
    warnedRef.current = false;

    // Aviso (ex: 8 min)
    warningRef.current = setTimeout(() => {
      if (!warnedRef.current) {
        warnedRef.current = true;
        onWarning();
      }
    }, timeoutMs - warningMs);

    // Logout automático (ex: 10 min)
    timeoutRef.current = setTimeout(() => {
      onTimeout();
    }, timeoutMs);
  }, [timeoutMs, warningMs, onWarning, onTimeout, clearTimers]);

  useEffect(() => {
    if (!enabled) {
      clearTimers();
      return;
    }

    resetTimers();

    ACTIVITY_EVENTS.forEach((event) =>
      window.addEventListener(event, resetTimers, { passive: true })
    );

    return () => {
      ACTIVITY_EVENTS.forEach((event) =>
        window.removeEventListener(event, resetTimers)
      );
      clearTimers();
    };
  }, [enabled, resetTimers, clearTimers]);
}

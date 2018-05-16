package robotambulance.util;

// Classe Semaphore utlisable sur NXT (contrairement à celle de l'API Java standard)
public class Semaphore {
	// Valeur du semaphore
	private int value;

	// Constructeur (Initialisation de la valeur
	public Semaphore(int init) {
		if (init < 0)
			init = 0;
		value = init;
	}

	// Acquérir un ticket du Semaphore
	public synchronized void acquire() {
		// Attente si la valeur est à 0
		while (value == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Si la valeur est positive, on décrémente la valeur
		value--;
	}

	public synchronized void release() {
		// On incrémente la valeur
		// On notifie les éventuels autres threads que le semaphore est positif
		value++;
		notify();
	}
}
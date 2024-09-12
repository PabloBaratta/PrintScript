package org.example;

import org.apache.commons.io.input.ObservableInputStream;

import java.io.IOException;
import java.io.InputStream;

public class Util {
	public static InputStream getObservableInputStream(InputStream inputStream) throws IOException {
		int totalBytes = inputStream.available();

		// Crear un ObservableInputStream para monitorear el progreso de la lectura
		ObservableInputStream observableInputStream =
				new ObservableInputStream(inputStream, new ObservableInputStream.Observer() {
			long bytesRead = 0;
			final int BAR_LENGTH = 50; // Longitud de la barra de progreso

			@Override
			public void data(int b) {
				bytesRead++;
				reportProgress(bytesRead, totalBytes);
			}

			@Override
			public void data(byte[] b, int offset, int length) {
				bytesRead += length;
				reportProgress(bytesRead, totalBytes);
			}

			// Método para reportar el progreso
			private void reportProgress(long bytesRead, int totalBytes) {
				double progressPercentage = ((double) bytesRead / totalBytes) * 100;
				int progressBars = (int) (progressPercentage / 100 * BAR_LENGTH);

				// Construimos la barra de progreso
				StringBuilder progressBar = new StringBuilder("[");
				for (int i = 0; i < BAR_LENGTH; i++) {
					if (i < progressBars) {
						progressBar.append("=");
					} else {
						progressBar.append(" ");
					}
				}
				progressBar.append("] ");

				// Añadir el porcentaje al final de la barra de progreso
				progressBar.append(String.format("%.2f%%", progressPercentage));

				// Saltar a una nueva línea para no sobrescribir el contenido impreso
				System.out.println(); // Nueva línea para no interferir con el texto anterior

				// Imprimir la barra de progreso en la nueva línea
				System.out.print(progressBar.toString());

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		// Imprimir una línea vacía para la barra de progreso antes de iniciar
		System.out.println(); // Esto asegura que la barra de progreso siempre esté visible desde el principio

		return observableInputStream;
	}


}

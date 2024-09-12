package org.example;

import org.apache.commons.io.input.ObservableInputStream;

import java.io.IOException;
import java.io.InputStream;

public class Util {
	public static InputStream getObservableInputStream(InputStream inputStream) throws IOException {
		int totalBytes = inputStream.available();

		// Secuencias ANSI para manipular el cursor
		final String ESC = "\033[";
		final String SAVE_CURSOR_POSITION = ESC + "s";   // Guarda la posición actual del cursor
		final String RESTORE_CURSOR_POSITION = ESC + "u"; // Restaura la posición guardada
		final String MOVE_CURSOR_UP = ESC + "1A";        // Mueve el cursor una línea arriba
		final String CLEAR_LINE = ESC + "2K";            // Borra la línea actual

		ObservableInputStream observableInputStream =
				new ObservableInputStream(inputStream, new ObservableInputStream.Observer() {
			long bytesRead = 0;
			final int BAR_LENGTH = 50;

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

			private void reportProgress(long bytesRead, int totalBytes) {
				double progressPercentage = ((double) bytesRead / totalBytes) * 100;
				int progressBars = (int) (progressPercentage / 100 * BAR_LENGTH);

				StringBuilder progressBar = new StringBuilder("[");
				for (int i = 0; i < BAR_LENGTH; i++) {
					if (i < progressBars) {
						progressBar.append("=");
					} else {
						progressBar.append(" ");
					}
				}
				progressBar.append("] ");

				progressBar.append(String.format("%.2f%%", progressPercentage));

				System.out.print(SAVE_CURSOR_POSITION + MOVE_CURSOR_UP + CLEAR_LINE);
				System.out.print(progressBar.toString());
				System.out.print(RESTORE_CURSOR_POSITION);
			}
		});

		System.out.println();
		return observableInputStream;
	}


}

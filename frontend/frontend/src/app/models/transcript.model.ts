export interface Transcript {
  id: number;
  recognitionServiceProvider: string;
  resultText: string;
  successful: boolean;
  errorMessage: string;
  createdOn: string;
}

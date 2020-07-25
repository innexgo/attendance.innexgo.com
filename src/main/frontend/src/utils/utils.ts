export async function fetchJson(url:string, params={}) {
  let response = await fetch(url, params);
  console.log(response);
  if (!response.ok) {
    throw Error(await response.text());
  }
  return response.json();
}

/**
 * Returns a promise that will be resolved in some milliseconds
 * use await sleep(some milliseconds)
 * @param {int} ms milliseconds to sleep for
 * @return {Promise} a promise that will resolve in ms milliseconds
 */
export function sleep(ms:number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export function staticUrl() {
  return window.location.protocol + "//" + window.location.host;
}

export function apiUrl() {
  return staticUrl() + '/api';
}

export async function fetchApi(url:string, params={}) {
	return await fetchJson(`${apiUrl()}/${url}`, params);
}

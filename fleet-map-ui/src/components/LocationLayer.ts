import type {Map as MapLibreMap} from 'maplibre-gl';import type {FeatureCollection} from '../types';
export function addLocationLayer(map:MapLibreMap,data:FeatureCollection){map.addSource('locations',{type:'geojson',data});map.addLayer({id:'locations',type:'circle',source:'locations',paint:{'circle-radius':7,'circle-color':'#eab308','circle-stroke-width':2,'circle-stroke-color':'#fff'}})}

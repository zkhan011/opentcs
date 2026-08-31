import type {Map as MapLibreMap} from 'maplibre-gl';import type {FeatureCollection} from '../types';
export function addPlannedRouteLayer(map:MapLibreMap,data:FeatureCollection){map.addSource('planned-route',{type:'geojson',data});map.addLayer({id:'planned-route',type:'line',source:'planned-route',paint:{'line-color':'#22d3ee','line-width':6,'line-opacity':.8}})}

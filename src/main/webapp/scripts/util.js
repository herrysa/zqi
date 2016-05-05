
function propertyFilterSearch(searchAreaId,gridId,noReload) {
	try{
		var sdata =$('#'+searchAreaId).serializeObject();
		//console.log(sdata);
		var postData = $("#"+gridId).jqGrid("getGridParam", "postData");
		$.extend(postData, sdata);
		//console.log(postData);
		$("#"+gridId).setGridParam({
			postData : postData
		});
		if(!noReload){
			$("#"+gridId).jqGrid("setGridParam", {
				search : true
			}).trigger("reloadGrid", [ {
					page : 1
			}]);
		}
	}catch(e){
		alert(e.message);
	}
}
$.fn.serializeObject = function() {
		var o = {};
		var a = this.serializeArray();
	//	console.log(a);
		var addFilters = false;
		$.each(a, function() {
			if(this.name.indexOf('_exclude_')==-1){
				var name = this.name ,v ;
				if(!this.value){
					v = '';
				}else{
					v = this.value;
					if(name.indexOf('_notInFilter')!=-1){
						name = name.replace('_notInFilter','');
					}else if(name.indexOf('{')!=-1){
						var nameRex = matchRegExp(name);
						if(nameRex){
							var n = nameRex[0].substring(1,nameRex[0].length-1);
							var vArr = v.split(',');
							var vStr = "";
							for(var vIdnex = 0;vIdnex<vArr.length;vIdnex++){
								var vTemp = vArr[vIdnex];
								vStr += "'"+vTemp+"',";
							}
							if(vStr){
								vStr = vStr.substring(0,vStr.length-1);
								v = n + " in ("+vStr+")";
								addFilters = true;
							}
						}
					}else{
						addFilters = true;
					}
				}
				if (o[name]) {
					if (!o[name].push) {
						o[name] = [ o[name] ];
					}
					o[name].push(v);
				} else {
					o[name] = v;
				}
			}
		});
		if (o['addFilters']) {
			if (!o['addFilters'].push) {
				o['addFilters'] = [ o['addFilters'] ];
			}
			o['addFilters'].push(addFilters);
		} else {
			o['addFilters'] = addFilters;
		}
		return o;
}
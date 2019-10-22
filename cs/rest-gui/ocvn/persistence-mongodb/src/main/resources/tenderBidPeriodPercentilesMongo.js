function(yearsStr,procuringEntityIdStr,bidTypeIdStr,bidSelectionMethod) {
   db.loadServerScripts();		
   if(yearsStr!=undefined) {	
	   var years=JSON.parse(yearsStr.replace(/'/g, ""));
	   matchArray=[];
	   for(var i in years) {                   
		   startStartDate=new ISODate(years[i]+"-01-01T00:00:00.000Z");
		   endStartDate=new ISODate(years[i]+"-12-31T00:00:00.000Z");
		   matchArray.push({ "tender.tenderPeriod.startDate" : { "$gte" : startStartDate , "$lte" : endStartDate  } });
		   match=  {  $or:  matchArray }  ;
	   }
   }
   else 
	  match =  { "tender.tenderPeriod.startDate" : { $ne : null }};
   
   if(procuringEntityIdStr!=undefined) {	
	   matchProcuringEntityId= {"tender.procuringEntity._id" : {$in : procuringEntityIdStr.split(",") }  };
   } else
	   matchProcuringEntityId={};
   
   //vietnam specific
   if(bidSelectionMethod!=undefined) {	
	   matchBidSelectionMethod= {"tender.succBidderMethodName" : {$in : bidSelectionMethod.split(",") }  };
   } else
	   matchBidSelectionMethod={};
	      
	      
   
   if(bidTypeIdStr!=undefined) {	
	   matchBidTypeId= {"tender.items.classification._id" : {$in : bidTypeIdStr.split(",") }  };
   } else
	   matchBidTypeId={};
   
 
   
	var agg = db.release.aggregate(
	[
	{ "$match" :
	{ 
		$and: [
		       {"tender.tenderPeriod.startDate" : { "$ne" : null} } , 
		       {"tender.tenderPeriod.endDate" : { "$ne" : null} },
		       match, matchProcuringEntityId, matchBidTypeId, matchBidSelectionMethod
		       ]
	    }
	} ,	
	{
	"$project" : {
	    "_id": false,
	        "tenderLengthDays" : {
	        $let: {
	            vars: {
	                endDate: "$tender.tenderPeriod.endDate",
	                startDate: "$tender.tenderPeriod.startDate"
	                },
	                in:  round ({  $divide : [ {$subtract: [   "$$endDate" ,  "$$startDate"  ]} , 86400000 ] },0)
	             }
	        }
	    }
	},
	{$sort: { tenderLengthDays : 1 }}
	]
	);
	
	var array=agg.toArray();	
	
	var len=array.length;
	
	result= { 
	    min: len>0 ? array[0]: 0, 
	    q1: len >0 ? array[Math.floor(len*.25)-1] : 0, 
	    median: len>0 ? array[Math.floor(len*.5)-1]:0 ,
	    q3: len>0 ? array[Math.floor(len*.75)-1]:0 ,
	    max:  len>0 ? array[len-1] : 0
	};

	return result;
}
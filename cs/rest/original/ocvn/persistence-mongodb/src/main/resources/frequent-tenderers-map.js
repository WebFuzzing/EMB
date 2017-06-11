function() {
    var tenderersSet = new Set();
    this.tender.tenderers.forEach(function(tenderer, it, array) {
        tenderersSet.add(tenderer._id);
    });

    var winnerId;
    this.awards.forEach(function(award, it, array) {
            if(award.status=="active")
                winnerId=award.suppliers[0]._id;
    });


    if (tenderersSet.size > 1) {
        var arr = Array.from(tenderersSet);
        for (var i = 0; i < arr.length; i++) {
            for (var j = i + 1; j < arr.length; j++)
                if (arr[i].localeCompare(arr[j]) > 0)
                    emit({
                        tendererId1: arr[i],
                        tendererId2: arr[j],
                    },
                    {
                        pairCount: 1,
                        winner1Count: arr[i]==winnerId?1:0,
                        winner2Count: arr[j]==winnerId?1:0
                    }
                    );
                else
                    emit({
                        tendererId1: arr[j],
                        tendererId2: arr[i]
                    },
                     {
                        pairCount: 1,
                        winner1Count: arr[j]==winnerId?1:0,
                        winner2Count: arr[i]==winnerId?1:0
                     }
                     );
        }
    }
};
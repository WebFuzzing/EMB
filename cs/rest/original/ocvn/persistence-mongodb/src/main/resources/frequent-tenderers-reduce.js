function(key, values) {

    var ret=
       {
            pairCount: 0,
            winner1Count: 0,
            winner2Count: 0
       }

    values.forEach(function(value) {
           ret.pairCount+=value.pairCount;
           ret.winner1Count+=value.winner1Count;
           ret.winner2Count+=value.winner2Count;
    });

    return ret;
}
//mongo does not know how to do rounding of numbers by itself, so we need a function for that
db.system.js.save(
                   { _id: "round",
                     value : 
                       function round(valueExpression, decimals) {
	var multiplier = Math.pow(10, decimals || 0);

	if (multiplier === 1) { // zero decimals
		return {
			$let: {
				vars: {
					valAdjusted: {
						$add: [
							valueExpression,
							{$cond: [{$gte: [valueExpression, 0]}, 0.5, -0.5]}
						]
					}
				},
				in: {
					$subtract: ['$$valAdjusted', {$mod: ['$$valAdjusted', 1]}]
				}
			}
		};
	}

	return {
		$let: {
			vars: {
				valAdjusted: {
					$add: [
						{$multiply: [valueExpression, multiplier]},
						{$cond: [{$gte: [valueExpression, 0]}, 0.5, -0.5]}
					]
				}
			},
			in: {
				$divide: [
					{$subtract: ['$$valAdjusted', {$mod: ['$$valAdjusted', 1]}]},
					multiplier
				]
			}
		}
	};
}
                       
                   }
                 )

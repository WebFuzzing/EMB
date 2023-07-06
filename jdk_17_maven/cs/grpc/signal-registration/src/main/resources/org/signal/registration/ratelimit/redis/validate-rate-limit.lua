-- A "leaky bucket" rate limiter controls the rate at which some action (identified by a given key) may be taken. This
-- implementation uses both a "permit regeneration" strategy (where a "bucket" has some maximum capacity for permits,
-- and the bucket refills over time) and "fixed rate" strategy, where actions can be taken at most once per unit time
-- even if the bucket has multiple permits available.
--
-- This implementation models buckets as "full" when they have permits available and "empty" when no permits are
-- available.
--
-- This script returns the duration in milliseconds before which an action will be allowed. If not positive, then the
-- action can be taken immediately. This script can be run in "read only" mode (see the `consumePermits` argument) to
-- check the time until an action is allowed without consuming permits or in "read/write" mode, where permits are
-- actually consumed.
local bucketId = KEYS[1]

local bucketSize = tonumber(ARGV[1])
local permitRegenerationMillis = tonumber(ARGV[2])
local minDelayMillis = tonumber(ARGV[3])
local currentTimeMillis = tonumber(ARGV[4])
local consumePermits = ARGV[5] and string.lower(ARGV[5]) == "true"
local requestedAmount = 1

local PERMITS_REMAINING_FIELD = "p"
local TIME_FIELD = "t"

local permitsRemaining
local lastUpdateTimeMillis
local remainingCooldown

if redis.call("EXISTS", bucketId) == 1 then
    local permitsRemainingStr, lastUpdateTimeMillisStr = unpack(redis.call("HMGET", bucketId, PERMITS_REMAINING_FIELD, TIME_FIELD))

    permitsRemaining = tonumber(permitsRemainingStr)
    lastUpdateTimeMillis = tonumber(lastUpdateTimeMillisStr)

    remainingCooldown = lastUpdateTimeMillis + minDelayMillis - currentTimeMillis
else
    permitsRemaining = bucketSize
    lastUpdateTimeMillis = currentTimeMillis
    remainingCooldown = 0
end

local elapsedTime = currentTimeMillis - lastUpdateTimeMillis
local availableAmount = math.min(
        bucketSize,
        permitsRemaining + (elapsedTime / permitRegenerationMillis)
)

if availableAmount >= requestedAmount and remainingCooldown <= 0 then
    if consumePermits then
        permitsRemaining = availableAmount - requestedAmount
        lastUpdateTimeMillis = currentTimeMillis

        local permitsUsed = bucketSize - permitsRemaining
        -- Storing a 'full' bucket (i.e. permitsUsed == 0) is equivalent of not storing any state at all
        -- (in which case a bucket will be just initialized from the input configs as a 'full' one).
        -- For this reason, we either set an expiration time on the record (calculated to let the bucket fully replenish)
        -- or we just delete the key if the bucket is full.
        if permitsUsed > 0 then
            local ttlMillis = math.max(remainingCooldown, math.ceil(permitsUsed * permitRegenerationMillis))
            redis.call("HSET", bucketId, PERMITS_REMAINING_FIELD, permitsRemaining, TIME_FIELD, lastUpdateTimeMillis)
            redis.call("PEXPIRE", bucketId, ttlMillis)
        else
            redis.call("DEL", bucketId)
        end
    end

    return 0
else
    local permitRegenerationTime = math.ceil((requestedAmount - availableAmount) * permitRegenerationMillis)

    return math.max(permitRegenerationTime, remainingCooldown)
end

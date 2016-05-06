package de.lostmekkasoft.suptow;

import com.badlogic.gdx.math.Vector2;

public class FabberModule extends EntityModule {
	
	public final float range, buildRate;
	
	private Entity harvestTarget = null;
	private Vector2 buildTargetPosition = null;

	public FabberModule(float range, float buildRate) {
		this.range = range;
		this.buildRate = buildRate;
	}

	public void stop() {
		harvestTarget = null;
		buildTargetPosition = null;
	}
	
	public void harvestResources(Entity resourcePoint) {
		if (resourcePoint.getResourcePointModule() == null) return;
		MovementModule mm = getEntity().getMovementModule();
		if (mm != null) mm.unsetTarget();
		if (!inRange(resourcePoint)) {
			if (mm == null) {
				return;
			} else {
				mm.setTarget(resourcePoint.getPosition(), range);
			}
		}
		harvestTarget = resourcePoint;
	}
	
	private void onHarvestArrive() {
		if (harvestTarget == null) return;
	}
	
	public void buildTower(Vector2 targetPosition) {
		MovementModule mm = getEntity().getMovementModule();
		if (!inRange(targetPosition)) {
			if (mm == null) {
				return;
			} else {
				mm.setTarget(targetPosition.cpy(), range, new TargetReachedCallback() {
					@Override
					public void run(Vector2 target) {
						onBuildArrive();
					}
				});
			}
		} else {
			if (mm != null) mm.unsetTarget();
			onBuildArrive();
		}
	}
	
	private void onBuildArrive() {
		getGame().createTower(buildTargetPosition);
	}
	
	private boolean inRange(Vector2 target) {
		return target.dst2(getPosition()) <= range * range;
	}

	private boolean inRange(Entity target) {
		return inRange(target.getPosition());
	}

	@Override
	public void update(float deltaTime) {
		if (buildTargetPosition != null) {
			// TODO: change to build tower over time
		} else if (harvestTarget != null) {
			if (inRange(harvestTarget)) {
				harvestFrom(harvestTarget, buildRate * deltaTime);
			}
		} else {
			// idle. see if we can reclaim stuff
			// TODO: look for things to reclaim
		}
	}
	
	private void harvestFrom(Entity e, float amount) {
		ResourcePointModule rm = e.getResourcePointModule();
		getGame().resources += rm.extract(amount);
	}
	
}

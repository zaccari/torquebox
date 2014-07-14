# Copyright 2014 Red Hat, Inc, and individual contributors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

CORE_DIR = "#{File.dirname(__FILE__)}/../../core"
$: << "#{CORE_DIR}/lib"
require "#{CORE_DIR}/spec/spec_helper"

require 'torquebox-messaging'

java_import java.util.concurrent.CountDownLatch
java_import java.util.concurrent.TimeUnit

class GetBroker
  extend TorqueBox::Messaging::Helpers
end

def random_queue
  TorqueBox::Messaging.queue(SecureRandom.uuid, durable: false)
end

def random_topic
  TorqueBox::Messaging.topic(SecureRandom.uuid)
end

RSpec.configure do |config|
  config.after(:suite) do
    GetBroker.send(:default_broker).stop
  end
end
